package uk.co.evanward.twitchinteractions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.evanward.twitchinteractions.command.TwitchCommand;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.config.WorldChanges;
import uk.co.evanward.twitchinteractions.helpers.FileHelper;
import uk.co.evanward.twitchinteractions.helpers.TwitchHelper;
import uk.co.evanward.twitchinteractions.twitch.event.hypetrain.HypeTrain;
import uk.co.evanward.twitchinteractions.twitch.server.SQLite;
import uk.co.evanward.twitchinteractions.twitch.websocket.SocketClient;

import java.lang.management.ManagementFactory;
import java.net.URI;

public class TwitchInteractions implements ModInitializer
{
    public static final String MOD_ID = "TwitchInteractions";
    public static final Logger logger = LoggerFactory.getLogger(MOD_ID);
    public static final HypeTrain hypeTrain = new HypeTrain();
    public static SocketClient socketClient;

    public static WorldChanges worldChanges = new WorldChanges();

    @Override
    public void onInitialize() {
        logger.info("Initialising Mod");

        ModConfig.loadConfig();

        // Perform actions once server loaded
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            FileHelper.loadPaths();

            // Load the world changes
            worldChanges = WorldChanges.getServerState(server);

            SQLite.initialiseSQLite();
        });

        socketClient = new SocketClient(URI.create(TwitchHelper.WEBSOCKET_ENDPOINT));

        CommandRegistrationCallback.EVENT.register(TwitchCommand::register);

        ServerTickEvents.START_SERVER_TICK.register(HypeTrain::tick);
    }

    /**
     * Check if the mod is running in debug mode
     */
    public static boolean isDebugMode()
    {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    }
}
