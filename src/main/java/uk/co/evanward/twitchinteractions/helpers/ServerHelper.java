package uk.co.evanward.twitchinteractions.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

import java.util.ArrayList;
import java.util.List;

public class ServerHelper
{
    /**
     * Get the player connected to Twitch
     */
    public static ServerPlayerEntity getConnectedPlayer()
    {
        return getServer().getPlayerManager().getPlayer(TwitchInteractions.socketClient.getPlayerId());
    }

    public static List<ServerPlayerEntity> getPlayers()
    {
        List<ServerPlayerEntity> players = new ArrayList<>();
        getServer().getWorlds().forEach(serverWorld -> players.addAll(serverWorld.getPlayers()));

        return players;
    }

    /**
     * Get the minecraft server
     */
    private static IntegratedServer getServer()
    {
        return MinecraftClient.getInstance().getServer();
    }
}
