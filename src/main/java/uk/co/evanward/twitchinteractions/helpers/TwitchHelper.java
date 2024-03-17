package uk.co.evanward.twitchinteractions.helpers;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class TwitchHelper
{
    private static final String[] SCOPES = {
        "moderator:read:followers", "channel:read:subscriptions", "bits:read", "channel:read:redemptions", "channel:read:hype_train", "channel:manage:redemptions"
    };

    public static final String AUTH_ENDPOINT = "https://id.twitch.tv/oauth2/authorize";

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
}
