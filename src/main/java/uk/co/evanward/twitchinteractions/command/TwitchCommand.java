package uk.co.evanward.twitchinteractions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

import static net.minecraft.server.command.CommandManager.literal;

public class TwitchCommand
{
    public static void register (CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment)
    {
        dispatcher.register(literal("twitch").executes(TwitchCommand::twitch)
            .then(literal("connect").executes(TwitchCommand::connect))
        );
    }

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

    private static int connect(CommandContext<ServerCommandSource> context)
    {
        if (!TwitchInteractions.socketClient.isConnected()) {
            try {
                TwitchInteractions.socketClient.connect(context.getSource());
            } catch (Exception e) {
                TwitchInteractions.logger.error("Error executing `twitch connect` command: " + e.getMessage());
            }
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Client is already connected to Twitch"), false);
        }

        return 1;
    }
}
