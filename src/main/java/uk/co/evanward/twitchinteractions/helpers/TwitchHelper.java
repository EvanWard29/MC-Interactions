package uk.co.evanward.twitchinteractions.helpers;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;
import uk.co.evanward.twitchinteractions.twitch.server.SQLite;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.UUID;

public class TwitchHelper
{
    private static final String[] SCOPES = {
        "moderator:read:followers", "channel:read:subscriptions", "bits:read", "channel:read:redemptions", "channel:read:hype_train", "channel:manage:redemptions"
    };

    public static final String AUTH_ENDPOINT = "https://id.twitch.tv/oauth2/authorize";
    public static final String API_ENDPOINT = "https://api.twitch.tv/helix";
    public static final String SUBSCRIPTION_ENDPOINT = (TwitchInteractions.isDebugMode() ? "http://localhost:8080" : API_ENDPOINT) + "/eventsub/subscriptions";
    public static final String WEBSOCKET_ENDPOINT = TwitchInteractions.isDebugMode() ? "ws://localhost:8080/ws" : "wss://eventsub.wss.twitch.tv/ws";

    private static final String CLIENT_ID = "uooaqxr6zz56gocr7x1anjh41j8z6t";

    public static URI getAuthUri(UUID playerUuid) {
        try {
            URIBuilder uri = new URIBuilder(AUTH_ENDPOINT);
            uri.addParameter("client_id", CLIENT_ID);
            uri.addParameter("response_type", "token");
            uri.addParameter("scope", String.join(" ", SCOPES));
            uri.addParameter("redirect_uri", "http://localhost:4567/twitch/auth/redirect");
            uri.addParameter("state", playerUuid.toString());

            return uri.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save the user's access token to config
     */
    public static void setAccessToken(String accessToken)
    {
        ModConfig.USER_ACCESS_TOKEN = accessToken;

        try {
            ModConfig.BROADCASTER_ID = getTwitchUserId();
        } catch (IOException | InterruptedException e) {
            TwitchInteractions.logger.error("Error getting Twitch broadcaster ID: " + e.getMessage());
        }

        try {
            ModConfig.saveConfig();
        } catch (IOException e) {
            TwitchInteractions.logger.error("Error saving configs to file: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Subscribe to the selected Twitch events in config
     */
    public static boolean subscribe()
    {
        // Check if Twitch has been authenticated first
        if (!authenticated()) {
            TwitchInteractions.logger.error("User is not authenticated with Twitch");

            return false;
        }

        for (TwitchEvent.Type type : ModConfig.TWITCH_EVENTS) {
            TwitchEvent.TwitchEventInterface event = new TwitchEvent(type).getEvent();

            if (event == null) {
                throw new RuntimeException("Unrecognised Twitch event: " + type);
            }

            JSONObject body = new JSONObject();
            body.put("type", type.getString());
            body.put("version", event.getVersion());
            body.put("condition", event.getCondition());
            body.put("transport", new JSONObject().put("method", "websocket").put("session_id", TwitchInteractions.socketClient.getSessionId()));

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBSCRIPTION_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Client-Id", CLIENT_ID)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ModConfig.USER_ACCESS_TOKEN)
                .build();

            String response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (IOException | InterruptedException e) {
                TwitchInteractions.logger.error("Error subscribing to event `" + event.getType().toString() + "`: " + e.getMessage());

                continue;
            }
            JSONObject responseBody = new JSONObject(response);

            if (responseBody.has("error")) {
                TwitchInteractions.logger.error("Error subscribing to event `" + event.getType().toString() + "`: " + response);
                TwitchInteractions.logger.info("REQUEST: " + body);
            } else {
                TwitchInteractions.logger.info("Subscribed to event: " + type.getString());
            }
        }

        return true;
    }

    /**
     * Get the follower list of the connected user
     */
    public static JSONObject getFollowerList(String after) throws Exception
    {
        // Check if Twitch has been authenticated first
        if (!TwitchHelper.authenticated()) {
            throw new Exception("User is not connected to Twitch");
        }

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(API_ENDPOINT + "/channels/followers?first=100&broadcaster_id=" + ModConfig.BROADCASTER_ID + "&after=" + after))
            .header("Accept", "application/json")
            .header("Client-Id", CLIENT_ID)
            .header("Authorization", "Bearer " + ModConfig.USER_ACCESS_TOKEN)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception(response.body());
        }

        return new JSONObject(response.body());
    }

    /**
     * Check if Twitch has been authenticated and the `broadcaster_id` and `access_token` has been set
     */
    public static boolean authenticated()
    {
        return !ModConfig.BROADCASTER_ID.isBlank() && !ModConfig.USER_ACCESS_TOKEN.isBlank();
    }

    /**
     * Check if the given follower has already followed before
     */
    public static boolean hasUserAlreadyFollowed(String followerId)
    {
        // Assume the follower hasn't followed before
        boolean followed = false;
        try {
            Connection connection = SQLite.connection();
            Statement statement = connection.createStatement();

            if (statement.execute("SELECT EXISTS(SELECT * FROM followers WHERE id = \"" + followerId + "\")")) {
                followed = statement.getResultSet().getBoolean(1);
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            TwitchInteractions.logger.error("Error checking if follower `" + followerId + "` is already following: " + e.getMessage());
        }

        return followed;
    }

    /**
     * Get the user's Twitch broadcasterId
     */
    private static String getTwitchUserId() throws IOException, InterruptedException
    {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_ENDPOINT + "/users"))
            .GET()
            .header("Accept", "application/json")
            .header("Client-Id", CLIENT_ID)
            .header("Authorization", "Bearer " + ModConfig.USER_ACCESS_TOKEN)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject responseBody = new JSONObject(response.body());

        return responseBody.getJSONArray("data").getJSONObject(0).getString("id");
    }
}
