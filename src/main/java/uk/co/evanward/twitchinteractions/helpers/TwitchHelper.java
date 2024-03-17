package uk.co.evanward.twitchinteractions.helpers;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.config.ModConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.UUID;

public class TwitchHelper
{
    private static final String[] SCOPES = {
        "moderator:read:followers", "channel:read:subscriptions", "bits:read", "channel:read:redemptions", "channel:read:hype_train", "channel:manage:redemptions"
    };

    public static final String AUTH_ENDPOINT = "https://id.twitch.tv/oauth2/authorize";
    public static final String API_ENDPOINT = "https://api.twitch.tv/helix";
    public static final String WEBSOCKET_ENDPOINT = TwitchInteractions.isDebugMode() ? "ws://localhost:8080/ws" : "wss://eventsub.wss.twitch.tv/ws";

    private static final String CLIENT_ID = "";

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
