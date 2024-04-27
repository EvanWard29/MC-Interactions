package uk.co.evanward.twitchinteractions.twitch.event.subscribe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
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

        // Set the Dolphin's egg name to the username if the gifter
        dolphinEgg.set(DataComponentTypes.ITEM_NAME, Text.literal(username));

        // Set the Dolphin's name to the username of the gifter
        NbtCompound entityData = new NbtCompound();
        entityData.putBoolean("CustomNameVisible", true);
        entityData.putString("CustomName", new JSONObject().put("text", username).toString());
        dolphinEgg.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));

        dolphinEgg.setCount(1);

        ServerHelper.giveItem(dolphinEgg);
    }
}
