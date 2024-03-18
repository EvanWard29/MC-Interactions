package uk.co.evanward.twitchinteractions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.TwitchHelper;
import uk.co.evanward.twitchinteractions.twitch.server.SparkServer;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class TwitchCommand
{
    public static void register (CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment)
    {
        dispatcher.register(literal("twitch").executes(TwitchCommand::twitch)
            .then(literal("connect").executes(TwitchCommand::connect))
            .then(literal("disconnect").executes(TwitchCommand::disconnect))
            .then(literal("authenticate").executes(TwitchCommand::authenticate))
        );
    }

    /**
     * Check the status of the Twitch websocket connection
     */
    private static int twitch(CommandContext<ServerCommandSource> context)
    {
        String sessionId = TwitchInteractions.socketClient.getSessionId();

        if (sessionId != null) {
            context.getSource().sendFeedback(() -> Text.literal("Connected to Twitch with session id `" + sessionId + "`"), false);
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Client is not connected to Twitch"), false);
        }

        return 1;
    }

    /**
     * Connect the client to the Twitch websocket
     */
    private static int connect(CommandContext<ServerCommandSource> context)
    {
        if (!TwitchInteractions.socketClient.isConnected()) {
            boolean connected = false;
            try {
                // Connect to Twitch and wait until success or failure
                connected = TwitchInteractions.socketClient.connectBlocking();
            } catch (InterruptedException e) {
                TwitchInteractions.logger.error("Error connecting to Twitch: " + e.getMessage());
            }

            if (connected) {
                context.getSource().sendFeedback(() -> Text.literal("Connected to Twitch"), false);

                TwitchInteractions.socketClient.setPlayerId(Objects.requireNonNull(context.getSource().getPlayer()).getUuid());
            } else {
                context.getSource().sendFeedback(() -> Text.literal("Could not connect to twitch"), false);

                return 0;
            }
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Client is already connected to Twitch"), false);
        }

        return 1;
    }

    /**
     * Disconnect the client from the Twitch websocket
     */
    private static int disconnect(CommandContext<ServerCommandSource> context)
    {
        if (TwitchInteractions.socketClient.isConnected()) {
            try {
                TwitchInteractions.socketClient.closeBlocking();
            } catch (InterruptedException e) {
                context.getSource().sendFeedback(() -> Text.literal("Error disconnecting from Twitch"), false);
                TwitchInteractions.logger.error("Error disconnecting from Twitch: " + e.getMessage());

                return 0;
            }

            if (!TwitchInteractions.socketClient.isConnected()) {
                context.getSource().sendFeedback(() -> Text.literal("Client successfully disconnected from Twitch"), false);
            } else {
                context.getSource().sendFeedback(() -> Text.literal("Error disconnecting from Twitch"), false);
                TwitchInteractions.logger.error("Error disconnecting from Twitch");
            }
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Client is not connected to Twitch"), false);
        }

        return 1;
    }

    /**
     * Start the authentication process with Twitch to retrieve an access token
     */
    private static int authenticate(CommandContext<ServerCommandSource> context)
    {
        // Start Spark server to listen for response
        SparkServer.start();

        // Generate Auth URI
        context.getSource().sendFeedback(() -> Text.literal("Click to Authorise")
                .fillStyle(Style.EMPTY.withClickEvent(
                    new ClickEvent(ClickEvent.Action.OPEN_URL,
                    TwitchHelper.getAuthUri(Objects.requireNonNull(context.getSource().getPlayer()).getUuid()).toString())
                ))
                .formatted(Formatting.YELLOW)
                .formatted(Formatting.UNDERLINE),
            false
        );

        return 1;
    }
}
