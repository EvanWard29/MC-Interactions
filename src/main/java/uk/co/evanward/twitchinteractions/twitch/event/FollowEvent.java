package uk.co.evanward.twitchinteractions.twitch.event;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.helpers.TwitchHelper;
import uk.co.evanward.twitchinteractions.twitch.server.SQLite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

    /**
     * Summon an allay
     */
    @Override
    public void trigger(JSONObject payload)
    {
        JSONObject follower = payload.getJSONObject("event");

        if (!TwitchHelper.hasUserAlreadyFollowed(follower.getString("user_id"))) {
            ServerPlayerEntity player = ServerHelper.getConnectedPlayer();
            AnnouncementHelper.playAnnouncement(follower.getString("user_name"), "Just Followed!");

            AllayEntity allay = new AllayEntity(EntityType.ALLAY, player.getWorld());
            allay.setPosition(player.getPos());
            allay.setCustomName(Text.literal(follower.getString("user_name")));
            allay.setCustomNameVisible(true);
            allay.getBrain().remember(MemoryModuleType.LIKED_PLAYER, player.getUuid());

            player.getServerWorld().spawnEntity(allay);

            try {
                // Add follower to DB
                Connection connection = SQLite.connection();
                Statement statement = connection.createStatement();

                String query = "INSERT INTO followers (id, user_login, user_name, followed_at) VALUES(\""
                    + follower.getString("user_id") + "\",\""
                    + follower.getString("user_login") + "\",\""
                    + follower.getString("user_name") + "\",\""
                    + follower.getString("followed_at") + "\")";

                statement.executeUpdate(query);

                statement.close();
                connection.close();
            } catch (SQLException e) {
                TwitchInteractions.logger.error("Error adding follower `" + follower.getString("user_name") + "` to DB: " + e.getMessage());
            }
        }
    }
}
