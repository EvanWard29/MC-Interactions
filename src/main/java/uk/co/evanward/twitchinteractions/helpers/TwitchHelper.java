package uk.co.evanward.twitchinteractions.helpers;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions.Action;
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
import java.util.Objects;
import java.util.Random;
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
            } else {
                TwitchInteractions.logger.info("Subscribed to event: " + type.getString() + " - " + responseBody.getJSONArray("data").getJSONObject(0).getString("id"));
            }
        }

        return true;
    }

    /** Unsubscribe from all currently subscribed events */
    public static void unsubscribe()
    {
        // Check if Twitch has been authenticated first
        if (!authenticated()) {
            TwitchInteractions.logger.error("User is not authenticated with Twitch");

            return;
        }

        HttpClient client = HttpClient.newHttpClient();

        // Get a list of subscribed events
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SUBSCRIPTION_ENDPOINT))
            .GET()
            .header("Client-Id", CLIENT_ID)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + ModConfig.USER_ACCESS_TOKEN)
            .build();

        String response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            TwitchInteractions.logger.error("Error getting subscribed events: " + e.getMessage());

            return;
        }

        JSONArray subscriptions = new JSONObject(response).getJSONArray("data");
        for (int i = 0; i < subscriptions.length(); i++) {
            JSONObject subscription = subscriptions.getJSONObject(i);

            HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(SUBSCRIPTION_ENDPOINT + "?id=" + subscription.getString("id")))
                .DELETE()
                .header("Client-Id", CLIENT_ID)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ModConfig.USER_ACCESS_TOKEN)
                .build();

            HttpResponse deleteResponse;
            try {
                deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                TwitchInteractions.logger.error("Error unsubscribing from event `" + subscription.getString("type") + "`: " + e.getMessage());

                continue;
            }

            if (deleteResponse.statusCode() != 204) {
                TwitchInteractions.logger.error("Error unsubscribing from event `" + subscription.getString("type") + "`: " + deleteResponse.body().toString());

                continue;
            }

            TwitchInteractions.logger.info("Unsubscribed from event `" + subscription.getString("type") + "`");
        }
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
        // Always return `false` when testing
        if (Objects.equals(followerId, "781930612")) {
            return false;
        }

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
     * Get a random Action enum from the given array of Action based on their weights
     */
    public static Action getRandomAction(Action[] actions)
    {
        int totalWeight = 0;

        for (Action action : actions) {
            totalWeight += action.getWeight();
        }

        // Get a random number between 1 and the total weight
        int random = (new Random()).nextInt(totalWeight);

        int cursor = 0;
        for (Action action : actions) {
            cursor += action.getWeight();

            if (cursor >= random) {
                return action;
            }
        }

        throw new RuntimeException("Error getting random action: Total weight may not add to 100");
    }

    /**
     * Refund the given channel point redemption
     */
    public static void refund(String redemptionId, String rewardId)
    {
        updateRedemptionStatus(redemptionId, rewardId, true);
    }

    /**
     * Confirm the given channel point redemption
     */
    public static void redeem(String redemptionId, String rewardId)
    {
        updateRedemptionStatus(redemptionId, rewardId, false);
    }

    /**
     * Set a channel point redemption to `FULFILLED` on success, or `CANCELED` on failure
     */
    private static void updateRedemptionStatus(String redemptionId, String rewardId, boolean refund)
    {
        if (!TwitchInteractions.isDebugMode()) {
            HttpClient client = HttpClient.newHttpClient();

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                    API_ENDPOINT
                    + "/channel_points/custom_rewards/redemptions"
                    + "?broadcaster_id=" + ModConfig.BROADCASTER_ID
                    + "&id=" + redemptionId
                    + "&reward_id=" + rewardId
                ))
                .method(
                    "PATCH",
                    HttpRequest.BodyPublishers.ofString(
                        new JSONObject()
                            .put("status", refund ? "CANCELED" : "FULFILLED")
                            .toString()
                    )
                )
                .header("Client-Id", CLIENT_ID)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ModConfig.USER_ACCESS_TOKEN)
                .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Throw error if response is not OK
                if (response.statusCode() != 200) {
                    throw new Exception(response.body());
                }
            } catch (Exception e) {
                TwitchInteractions.logger.error("Error updating redemption status for redemption `" + redemptionId + "`: " + e.getMessage());
            }
        }
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
