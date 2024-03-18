package uk.co.evanward.twitchinteractions.twitch.event;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.config.ModConfig;

public class FollowEvent implements TwitchEvent.TwitchEventInterface
{
    private final TwitchEvent.Type type;
    private final String version;
    private final JSONObject context;

    public FollowEvent()
    {
        this.type = TwitchEvent.Type.FOLLOW;
        this.version = "2";
        this.context = new JSONObject()
            .put("broadcaster_user_id", ModConfig.BROADCASTER_ID)
            .put("moderator_user_id", ModConfig.BROADCASTER_ID);
    }

    @Override
    public TwitchEvent.Type getType()
    {
        return this.type;
    }

    @Override
    public String getVersion()
    {
        return this.version;
    }

    @Override
    public JSONObject getCondition()
    {
        return this.context;
    }

    @Override
    public void trigger(JSONObject payload)
    {

    }
}
