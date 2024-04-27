package uk.co.evanward.twitchinteractions.twitch.event.subscribe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;

import java.util.Random;

public class SubscribeEvent implements TwitchEvent.TwitchEventInterface
{
    private enum Tier
    {
        TIER_1000, TIER_2000, TIER_3000;

        /**
         * Get the relevant entity to spawn based on the tier
         */
        public Entity getEntity(ServerPlayerEntity player)
        {
            switch (this) {
                case TIER_1000 -> {
                    // Random cat variant
                    CatEntity cat = new CatEntity(EntityType.CAT, player.getEntityWorld());
                    cat.setOwner(player);

                    CatVariant variant = Registries.CAT_VARIANT.get((new Random()).nextInt(Registries.CAT_VARIANT.size()));
                    cat.setVariant(Registries.CAT_VARIANT.getEntry(variant));

                    return cat;
                }
                case TIER_2000 -> {
                    // Random wolf variant
                    WolfEntity wolf = new WolfEntity(EntityType.WOLF, player.getEntityWorld());
                    wolf.setOwner(player);

                    NbtCompound collar = new NbtCompound();
                    collar.putInt("CollarColor", (new Random()).nextInt(DyeColor.values().length));
                    wolf.readCustomDataFromNbt(collar);

                    return wolf;
                }
                case TIER_3000 -> {
                    // Random panda variant
                    PandaEntity panda = new PandaEntity(EntityType.PANDA, player.getEntityWorld());
                    panda.setMainGene(PandaEntity.Gene.createRandom(net.minecraft.util.math.random.Random.create((new Random()).nextLong())));
                    panda.setHiddenGene(PandaEntity.Gene.createRandom(net.minecraft.util.math.random.Random.create((new Random()).nextLong())));

                    return panda;
                }
                default -> throw new RuntimeException("Unrecognised `Tier` enum `" + this + "`");
            }
        }
    }

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
            // <editor-fold desc="Gift Sub">
            SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(EntityType.TROPICAL_FISH);
            ItemStack eggItem = spawnEggItem.asItem().getDefaultStack();

            // Set the name of the egg to the giftee's username
            eggItem.set(DataComponentTypes.ITEM_NAME, Text.literal(event.getString("user_name")));

            // Set the name of the Fish to the giftee's username
            NbtCompound entityData = new NbtCompound();
            entityData.putString("CustomName", new JSONObject().put("text", event.getString("user_name")).toString());
            entityData.putBoolean("CustomNameVisible", true);
            eggItem.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));

            eggItem.setCount(1);

            ServerHelper.giveItem(eggItem);
            // </editor-fold>
        } else {
            AnnouncementHelper.playAnnouncement(event.getString("user_name"), "Just Subscribed!");

            Tier tier = Tier.valueOf("TIER_" + event.getString("tier"));

            ServerPlayerEntity player = ServerHelper.getConnectedPlayer();

            Entity entity = tier.getEntity(player);
            entity.setPosition(player.getPos());
            entity.setCustomName(Text.literal(event.getString("user_name")));
            entity.setCustomNameVisible(true);

            player.getServerWorld().spawnEntity(entity);
        }
    }
}
