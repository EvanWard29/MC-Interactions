package uk.co.evanward.twitchinteractions.twitch.event;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

public class SubscribeEvent implements TwitchEvent.TwitchEventInterface
{
    @Override
    public TwitchEvent.Type getType()
    {
        return TwitchEvent.Type.SUBSCRIBE;
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
     * Summon:
     * <ul>
     *     <li>Tropical Fish - Gift Sub</li>
     *     <li>Cat - Tier 1</li>
     *     <li>Wolf - Tier 2</li>
     *     <li>Panda - Tier 3</li>
     * </ul>
     */
    @Override
    public void trigger(JSONObject payload)
    {
        JSONObject event = payload.getJSONObject("event");

        if (event.has("is_gift") && event.getBoolean("is_gift")) {
            // Gift Sub
            SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(EntityType.TROPICAL_FISH);
            ItemStack eggItem = spawnEggItem.asItem().getDefaultStack();
            eggItem.setCount(1);

            NbtCompound entityNbt = new NbtCompound();
            entityNbt.putBoolean("CustomNameVisible", true);
            entityNbt.putString("CustomName", new JSONObject().put("text", event.getString("user_name")).toString());

            NbtCompound itemName = new NbtCompound();
            itemName.putString("Name", "{\"text\":\"" + event.getString("user_name") + "\"}");

            NbtCompound nbt = new NbtCompound();
            nbt.put("EntityTag", entityNbt);
            nbt.put("display", itemName);

            eggItem.setNbt(nbt);

            ServerHelper.getConnectedPlayer().giveItemStack(eggItem);
        }
    }
}
