package uk.co.evanward.twitchinteractions.twitch.server.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class SocketClient extends WebSocketClient
{
    public SocketClient(URI serverUri)
    {
        super(serverUri);
    }

    /**
     * On connection with Twitch websocket
     *
     * @param handshakeData The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshakeData)
    {

    }

    /**
     * Triggered on subscribed Twitch event
     *
     * @param message Contains JSON sent by the Twitch websocket
     */
    @Override
    public void onMessage(String message)
    {
        JSONObject data = null;
        try {
            data = new JSONObject(message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String type = null;
        try {
            type = data.getJSONObject("metadata").getString("message_type");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        switch (type) {
            case "session_welcome" -> {

            }
            case "message" -> {

            }
            case "session_reconnect" -> {

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
            case -1:
                // Connection refused
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
        }
    }

    /**
     * Triggers when an exception is thrown by the websocket
     */
    @Override
    public void onError(Exception ex)
    {

    }
}
