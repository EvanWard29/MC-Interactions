package uk.co.evanward.twitchinteractions;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.evanward.twitchinteractions.twitch.server.websocket.SocketClient;

import java.net.URI;

public class TwitchInteractions implements ModInitializer
{
    public static final Logger logger = LoggerFactory.getLogger(TwitchInteractions.class);
    public static SocketClient socketClient;

    @Override
    public void onInitialize() {
        logger.info("Initialising Mod");

        socketClient = new SocketClient(URI.create("ws://localhost:8000/ws"));
    }
}
