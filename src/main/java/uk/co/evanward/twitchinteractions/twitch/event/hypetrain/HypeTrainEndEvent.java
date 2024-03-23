package uk.co.evanward.twitchinteractions.twitch.event.hypetrain;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;

public class HypeTrainEndEvent implements TwitchEvent.TwitchEventInterface
{
    @Override
    public TwitchEvent.Type getType()
    {
        return TwitchEvent.Type.HYPE_TRAIN_END;
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
     * Stop spawning mobs every 2-3 seconds
     */
    @Override
    public void trigger(JSONObject payload)
    {
        if (TwitchInteractions.hypeTrain.isActive()) {
            TwitchInteractions.hypeTrain.end();
        }
    }
}
