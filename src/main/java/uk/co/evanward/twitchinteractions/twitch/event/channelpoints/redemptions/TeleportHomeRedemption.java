package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Optional;

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

        // Get the player's spawn
        Optional<Vec3d> spawnPoint = PlayerEntity.findRespawnPosition(
            ServerHelper.getServer().getWorld(World.OVERWORLD),
            player.getSpawnPointPosition(),
            player.getSpawnAngle(),
            false,
            false
        );

        // Spawn the player at their spawn point, or the world spawn if not set
        Vec3d spawn;
        if (spawnPoint.isPresent()) {
            spawn = spawnPoint.get();
        } else {
            BlockPos blockPos = ServerHelper.getServer().getWorld(World.OVERWORLD).getSpawnPos();
            spawn = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

        // Teleport the player to the Overworld spawn
        player.teleport(ServerHelper.getServer().getWorld(World.OVERWORLD), spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());

        // Play the announcement sound at the Overworld spawn
        player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 0.5f);
    }
}
