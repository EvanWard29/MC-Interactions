package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

public class TeleportHomeRedemption implements ChannelPoint.ChannelPointInterface
{
    /**
     * Teleport the connected player to the World Spawn of the current dimension
     */
    @Override
    public void trigger(JSONObject event)
    {
        AnnouncementHelper.playAnnouncement(event.getString("user_name"), "Redeemed Teleport Home!", false);

        // Get the connected player
        ServerPlayerEntity player = ServerHelper.getConnectedPlayer();

        // Get the Overworld of the server
        ServerWorld overworld = ServerHelper.getServer().getWorld(World.OVERWORLD);

        // Get the coords of the Overworld spawn
        BlockPos spawn = overworld.getSpawnPos();

        // Teleport the player to the Overworld spawn
        player.teleport(overworld, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());

        // Play the announcement sound at the Overworld spawn
        overworld.playSound(player, spawn, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 0.5f);
    }
}
