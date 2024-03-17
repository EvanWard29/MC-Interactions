package uk.co.evanward.twitchinteractions.twitch.server.websocket;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

import java.net.URI;

public class SocketClient extends WebSocketClient
{
    private String sessionId;

    public SocketClient(URI serverUri)
    {
        super(serverUri);
    }

    public void connect(ServerCommandSource commandSource)
    {
        commandSource.sendFeedback(() -> Text.literal("Connecting to Twitch..."), false);

        boolean connected;
        try {
            connected = connectBlocking();
        } catch (InterruptedException e) {
            commandSource.sendFeedback(() -> Text.literal("Error connecting to Twitch: " + e.getMessage()), false);
            return;
        }

        if (connected) {
            commandSource.sendFeedback(() -> Text.literal("Connected to Twitch"), false);
        } else {
            commandSource.sendFeedback(() -> Text.literal("Could not connect to twitch"), false);
        }
    }

    /**
     * On connection with Twitch websocket
     *
     * @param handshakeData The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshakeData)
    {
        TwitchInteractions.logger.info("Successful connection to Twitch");
    }

    /**
     * Triggered on subscribed Twitch event
     *
     * @param message Contains JSON sent by the Twitch websocket
     */
    @Override
    public void onMessage(String message)
    {
        JSONObject data;
        String type;

        try {
            data = new JSONObject(message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            type = data.getJSONObject("metadata").getString("message_type");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        switch (type) {
            case "session_welcome" -> {
                // Set session id
                try {
                    this.sessionId = data.getJSONObject("payload").getJSONObject("session").getString("id");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                TwitchInteractions.logger.info("Connected to Twitch with session ID `" + this.sessionId + "`");
            }
            case "notification" -> {
                JSONObject payload;
                try {
                    payload = data.getJSONObject("payload");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                try {
                    TwitchInteractions.logger.info("Twitch event: " + payload.getJSONObject("subscription").getString("type"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            case "session_reconnect" -> {
                TwitchInteractions.logger.warn("Twitch socket triggered reconnect");
            }
        }
    }

    /**
     * Triggered when the websocket disconnects from Twitch
     *
     * @param code Details the reason for the websocket disconnecting
     *             - {@link <a href="https://dev.twitch.tv/docs/eventsub/handling-websocket-events/#close-message">...</a>}
     */
    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        switch (code) {
            case 1000:
                // Successful client disconnect
                break;
            case 4000:
                // Internal server error
                break;
            case 4001:
                // Client sent inbound traffic
                break;
            case 4003:
                // Client failed ping-pong
                break;
            case 4004:
                // Reconnect grace time expired
                break;
            case 4005:
                // Network timeout
                break;
            case 4006:
                // Network error
                break;
            case 4007:
                // Invalid reconnect
                break;
            default:
                // Other
                TwitchInteractions.logger.error(reason);
        }
    }

    /**
     * Triggers when an exception is thrown by the websocket
     */
    @Override
    public void onError(Exception ex)
    {

    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public boolean isConnected()
    {
        return this.sessionId != null;
    }
}
