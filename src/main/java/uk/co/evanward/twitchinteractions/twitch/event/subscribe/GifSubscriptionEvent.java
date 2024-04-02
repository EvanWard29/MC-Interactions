package uk.co.evanward.twitchinteractions.twitch.event.subscribe;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;

public class GifSubscriptionEvent implements TwitchEvent.TwitchEventInterface
{
    @Override
    public TwitchEvent.Type getType()
    {
        return TwitchEvent.Type.GIFT;
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
        JSONObject event = payload.getJSONObject("event");

        String username = !event.getBoolean("is_anonymous") ? event.getString("user_name") : "A Cool User";

        AnnouncementHelper.playAnnouncement(username, "Just Gifted Subscriptions!");

        SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(EntityType.DOLPHIN);
        ItemStack dolphinEgg = spawnEggItem.asItem().getDefaultStack();

        NbtCompound entityNbt = new NbtCompound();
        entityNbt.putBoolean("CustomNameVisible", true);
        entityNbt.putString("CustomName", new JSONObject().put("text", username).toString());

        NbtCompound itemName = new NbtCompound();
        itemName.putString("Name", "{\"text\":\"" + username + "\"}");

        NbtCompound nbt = new NbtCompound();
        nbt.put("EntityTag", entityNbt);
        nbt.put("display", itemName);

        dolphinEgg.setNbt(nbt);
        dolphinEgg.setCount(1);

        ServerHelper.giveItem(dolphinEgg);
    }
}
