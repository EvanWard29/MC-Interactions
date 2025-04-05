package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
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

        // Spawn the player at their spawn point
        BlockPos spawn = player.getSpawnPointPosition();

        // Spawn the player at the world spawn if not set
        if (spawn == null) {
            spawn = ServerHelper.getServer().getWorld(World.OVERWORLD).getSpawnPos();
        }

        // Teleport the player to the Overworld spawn
        player.teleport(ServerHelper.getServer().getWorld(World.OVERWORLD), spawn.getX(), spawn.getY(), spawn.getZ(), PositionFlag.VALUES, player.getYaw(), player.getPitch(), false);

        // Play the announcement sound at the Overworld spawn
        player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 0.5f);
    }
}
