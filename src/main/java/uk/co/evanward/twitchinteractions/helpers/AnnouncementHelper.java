package uk.co.evanward.twitchinteractions.helpers;

import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AnnouncementHelper
{
    /**
     * Play an announcement triggered by a Twitch event
     */
    public static void playAnnouncement(String title, String subtitle)
    {
        playAnnouncement(title, subtitle, true);
    }

    /**
     * Play an announcement triggered by a Twitch event
     */
    public static void playAnnouncement(String title, String subtitle, boolean playSound)
    {
        ServerPlayerEntity player = ServerHelper.getConnectedPlayer();
        announce(player, title, subtitle, playSound);
    }

    /**
     * Play an announcement triggered by a Twitch event
     */
    public static void playAnnouncement(String title, String subtitle, boolean playSound, boolean broadcast)
    {
        if (broadcast) {
            // Play the announcement for all players
            ServerHelper.getPlayers().forEach(player -> {
                announce(player, title, subtitle, playSound);
            });
        } else {
            // Play the announcement for just the connected player
            playAnnouncement(title, subtitle, playSound);
        }
    }

    private static void announce(ServerPlayerEntity player, String title, String subtitle, boolean playSound)
    {
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal(subtitle).formatted(Formatting.YELLOW)));
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal(title).formatted(Formatting.AQUA)));

        // Play the announcement sound
        if (playSound) {
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 0.5f);
        }
    }
}
