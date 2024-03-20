package uk.co.evanward.twitchinteractions.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
    public static IntegratedServer getServer()
    {
        return MinecraftClient.getInstance().getServer();
    }

    /**
     * Give a player the given item
     */
    public static void giveItem(ServerPlayerEntity player, ItemStack item)
    {
        // Attempt to insert egg into player's inventory
        boolean inserted = player.getInventory().insertStack(item);

        // Drop the egg on the floor if the player's inventory is full
        ItemEntity itemEntity;
        if (!inserted) {
            itemEntity = player.dropItem(item, false);
            itemEntity.resetPickupDelay();
            itemEntity.setOwner(player.getUuid());
        }

        // Play item pickup sound
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
        player.currentScreenHandler.sendContentUpdates();
    }
}
