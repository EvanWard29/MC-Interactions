package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.world.World;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

public class SetNightRedemption implements ChannelPoint.ChannelPointInterface
{
    /**
     * Set the time in the Overworld to Night
     */
    @Override
    public void trigger(JSONObject event)
    {
        AnnouncementHelper.playAnnouncement(event.getString("user_name"), "Turned It To Night!");

        ServerHelper.getServer().getWorld(World.OVERWORLD).setTimeOfDay(13000);
    }
}
