package uk.co.evanward.twitchinteractions.twitch.event;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

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
        JSONObject event = payload.getJSONObject("event");
        String follower = event.getString("user_name");

        ServerPlayerEntity player = ServerHelper.getConnectedPlayer();
        AnnouncementHelper.playAnnouncement(follower, "Just Followed!");

        AllayEntity allay = new AllayEntity(EntityType.ALLAY, player.getWorld());
        allay.setPosition(player.getPos());
        allay.setCustomName(Text.literal(follower));
        allay.setCustomNameVisible(true);
        allay.getBrain().remember(MemoryModuleType.LIKED_PLAYER, player.getUuid());

        player.getServerWorld().spawnEntity(allay);
    }
}
