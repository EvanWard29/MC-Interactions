package uk.co.evanward.twitchinteractions.twitch.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.TwitchHelper;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;

import java.net.URI;
import java.util.UUID;

public class SocketClient extends WebSocketClient
{
    private String sessionId;
    private UUID playerId;

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
        String messageType;

        try {
            data = new JSONObject(message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            messageType = data.getJSONObject("metadata").getString("message_type");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        switch (messageType) {
            case "session_welcome" -> {
                // Set session id
                try {
                    this.sessionId = data.getJSONObject("payload").getJSONObject("session").getString("id");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Subscribe to events
                boolean subscribed = TwitchHelper.subscribe();

                if (subscribed) {
                    TwitchInteractions.logger.info("Connected to Twitch with session ID `" + this.sessionId + "`");
                } else {
                    TwitchInteractions.logger.error("Could not connect to Twitch: Subscriptions failed");

                    TwitchInteractions.socketClient.close();
                    TwitchInteractions.socketClient = new SocketClient(URI.create(TwitchHelper.WEBSOCKET_ENDPOINT));
                }
            }
            case "notification" -> {
                JSONObject payload;
                try {
                    payload = data.getJSONObject("payload");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                TwitchEvent.Type type;
                try {
                    type = TwitchEvent.Type.from(payload.getJSONObject("subscription").getString("type"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                new TwitchEvent(type).getEvent().trigger(payload);

                try {
                    TwitchInteractions.logger.info("Twitch event: " + type);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            case "session_reconnect" -> {
                TwitchInteractions.logger.warn("Twitch socket triggered reconnect");

                // Keep the old socket active until successful reconnect
                SocketClient oldSocket = TwitchInteractions.socketClient;

                String uri;
                try {
                    uri = data.getJSONObject("payload").getJSONObject("session").getString("reconnect_url");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Create a new socket for reconnecting
                TwitchInteractions.socketClient = new SocketClient(URI.create(uri));

                boolean connected;
                try {
                    connected = TwitchInteractions.socketClient.connectBlocking();
                } catch (InterruptedException e) {
                    TwitchInteractions.logger.error("Error reconnecting to Twitch: " + e.getMessage());

                    return;
                }

                // Close the old socket if successfully reconnected
                if (connected) {
                    oldSocket.close();
                } else {
                    TwitchInteractions.logger.error("Error reconnecting to Twitch");
                }
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
        // Successful client disconnect
        if (code == CloseFrame.NORMAL) {
            TwitchInteractions.logger.info("Successfully disconnected from Twitch");

            // Create a new SocketClient instance to allow reconnecting
            TwitchInteractions.socketClient = new SocketClient(URI.create("ws://localhost:8080/ws"));

            return;
        }

        // Unexpected disconnect
        switch (code) {
            case 4000 -> {
                // Internal server error
                TwitchInteractions.logger.error("Internal server error: Indicates a problem with the server");
            }
            case 4001 -> {
                // Client sent inbound traffic
                TwitchInteractions.logger.error("Client sent inbound traffic: Sending outgoing messages to the server is prohibited with the exception of pong messages");
            }
            case 4002 -> {
                // Client failed ping-pong
                TwitchInteractions.logger.error("Client failed ping-pong: You must respond to ping messages with a pong message");
            }
            case 4003 -> {
                // Connection unused
                TwitchInteractions.logger.error("Connection unused: When you connect to the server, you must create a subscription within 10 seconds or the connection is closed");
            }
            case 4004 -> {
                // Reconnect grace time expired
                TwitchInteractions.logger.error("Reconnect grace time expired: When you receive a session_reconnect message, you have 30 seconds to reconnect to the server and close the old connection");
            }
            case 4005 -> {
                // Network timeout
                TwitchInteractions.logger.error("Network timeout: Transient network timeout");
            }
            case 4006 -> {
                // Network error
                TwitchInteractions.logger.error("Network error: Transient network error");
            }
            case 4007 -> {
                // Invalid reconnect
                TwitchInteractions.logger.error("Invalid reconnect: The reconnect URL is invalid");
            }
            default -> {
                // Other
                TwitchInteractions.logger.error("Unexpected socket error: " + reason);
            }
        }

        // Create a new SocketClient instance to allow reconnecting
        TwitchInteractions.socketClient = new SocketClient(URI.create("ws://localhost:8080/ws"));

        // Warn connected players of unexpected disconnect
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

    /**
     * Set the connected player's UUID
     */
    public void setPlayerId(UUID playerId)
    {
        this.playerId = playerId;
    }

    /**
     * Get the connected player's UUID
     */
    public UUID getPlayerId()
    {
        return this.playerId;
    }
}
