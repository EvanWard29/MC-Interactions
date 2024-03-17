package uk.co.evanward.twitchinteractions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;

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
        context.getSource().sendFeedback(() -> Text.literal("RETURNS CONNECTION STATUS"), false);

        return 1;
    }

    private static int connect(CommandContext<ServerCommandSource> context)
    {
        context.getSource().sendFeedback(() -> Text.literal("HELLO WORLD"), false);

        return 1;
    }
}
