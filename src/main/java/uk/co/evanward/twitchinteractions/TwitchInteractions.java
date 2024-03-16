package uk.co.evanward.twitchinteractions;

import net.fabricmc.api.ModInitializer;
import uk.co.evanward.twitchinteractions.twitch.server.websocket.SocketClient;

import java.net.URI;

public class TwitchInteractions implements ModInitializer
{
    public static SocketClient socketClient;

    @Override
    public void onInitialize() {
        socketClient = new SocketClient(URI.create("ws://localhost:8000/ws"));
    }
}
