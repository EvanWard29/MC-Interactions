package uk.co.evanward.twitchinteractions.twitch.event.channelpoints;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;

public class ChannelPointRedemptionEvent implements TwitchEvent.TwitchEventInterface
{
    @Override
    public TwitchEvent.Type getType()
    {
        return TwitchEvent.Type.CHANNEL_POINTS_REDEMPTION;
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

    @Override
    public void trigger(JSONObject payload)
    {
        JSONObject reward = payload.getJSONObject("event").getJSONObject("reward");

        new ChannelPoint(reward.getString("id")).getRedemption().trigger(payload.getJSONObject("event"));

        // Update the redemption status
    }
}
