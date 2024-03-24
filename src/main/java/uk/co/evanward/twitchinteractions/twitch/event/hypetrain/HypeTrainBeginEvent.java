package uk.co.evanward.twitchinteractions.twitch.event.hypetrain;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;

public class HypeTrainBeginEvent implements TwitchEvent.TwitchEventInterface
{
    @Override
    public TwitchEvent.Type getType()
    {
        return TwitchEvent.Type.HYPE_TRAIN_START;
    }

    @Override
    public String getVersion()
    {
        return "1";
    }

    @Override
    public JSONObject getCondition()
    {
        return new JSONObject()
            .put("broadcaster_user_id", ModConfig.BROADCASTER_ID);
    }

    /**
     * Spawn mobs every 2-3 seconds of different difficulty, based on the Hype Train level
     */
    @Override
    public void trigger(JSONObject payload)
    {
        // Only start a hype train if not already active
        if (!TwitchInteractions.hypeTrain.isActive()) {
            AnnouncementHelper.playAnnouncement("A Hype Train Has Begun!", "");
            TwitchInteractions.hypeTrain.start(HypeTrain.Level.from(payload.getJSONObject("event").getInt("level")));
        }
    }
}
