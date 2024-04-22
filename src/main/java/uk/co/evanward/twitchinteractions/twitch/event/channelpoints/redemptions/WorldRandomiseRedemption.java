package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class WorldRandomiseRedemption implements ChannelPoint.ChannelPointInterface
{
    /**
     * Change the following of the world:
     * <ul>
     *     <li>Item laid by chickens</li>
     *     <li>Natural mob spawns</li>
     *     <li>Amount outputted by a crafting recipe</li>
     *     <li>Dropped loot</li>
     *     <li>Double or half the max item stack size</li>
     *     <li>Replace sound with another</li>
     *     <li>Villager currency</li>
     *     <li>Length of day</li>
     *     <li>Sheep colour</li>
     *     <li>Item use speed</li>
     *     <li>Loot double or half</li>
     *     <li>Recipe output double or half</li>
     *     <li>Spawn egg chance</li>
     *     <li>Item despawn time</li>
     *     <li>Damage modifier - Change the damage value of a random damage source</li>
     * </ul>
     */
    @Override
    public void trigger(JSONObject event)
    {
        this.changeChickenEgg();

        this.changeLengthOfDay();

        this.changeSheepColour();

        this.changeDespawnTime();

        this.changeDamageModifier();

        this.changeSpawnEggChance();

        this.changeLootDrop();

        this.changeLootAmount();

        this.changeMobSpawn();

        TwitchInteractions.worldChanges.setDirty(true);
    }

    /**
     * Set the item Chickens lay to a random one
     */
    private void changeChickenEgg()
    {
        TwitchInteractions.worldChanges.CHICKEN_EGG = ServerHelper.getServer()
            .getRegistryManager()
            .get(RegistryKeys.ITEM)
            .getRandom(ServerHelper.getConnectedPlayer().getRandom())
            .get()
            .value();
    }

    /**
     * Double or half the length of day
     */
    private void changeLengthOfDay()
    {
        int newDayLength = (new Random()).nextBoolean()
            ? TwitchInteractions.worldChanges.DAY_LENGTH * 2
            : TwitchInteractions.worldChanges.DAY_LENGTH / 2;

        // Reset to 24000 ticks (20:00) if day is less than 1500 ticks (1:15)
        TwitchInteractions.worldChanges.DAY_LENGTH = newDayLength < 1500
            ? 24000
            : newDayLength;
    }

    /**
     * Change the colour of naturally spawning sheep
     */
    private void changeSheepColour()
    {
        TwitchInteractions.worldChanges.SHEEP_COLOUR = DyeColor.values()[new Random().nextInt(DyeColor.values().length)];
    }

    /**
     * Double or half the item despawn time
     */
    private void changeDespawnTime()
    {
        // Double or half the item despawn time
        int despawnTime = (new Random()).nextBoolean()
            ? TwitchInteractions.worldChanges.ITEM_DESPAWN * 2
            : TwitchInteractions.worldChanges.ITEM_DESPAWN / 2;

        // 10% chance to reset the timer
        if ((new Random()).nextInt(100) <= 10) {
            despawnTime = 6000;
        }

        // Set the despawn time to 6000 (5:00) if less than 200 (00:05)
        TwitchInteractions.worldChanges.ITEM_DESPAWN = despawnTime < 600
            ? 6000
            : despawnTime;
    }

    /**
     * Modify the damage of a random damage source
     */
    private void changeDamageModifier()
    {
        // Get a random damage type
        DamageType damageType = ServerHelper.getConnectedPlayer()
            .getDamageSources()
            .registry
            .getRandom(ServerHelper.getConnectedPlayer().getRandom())
            .get()
            .value();

        // Get the existing modifier of the random damage type (if set already)
        float damageModifier = TwitchInteractions.worldChanges.DAMAGE_MODIFIERS.getFloat(damageType.toString());

        // Set the modifier to unmodified if not already modified
        if (!(damageModifier > 0.0f)) {
            damageModifier = 1.0f;
        }

        // Double or half the damage modifier
        TwitchInteractions.worldChanges.DAMAGE_MODIFIERS.putFloat(
            damageType.msgId(),
            (new Random()).nextBoolean()
                ? damageModifier * 2
                : damageModifier / 2
        );
    }

    /**
     * Set the chance of a spawn egg dropping when killing a mob
     */
    private void changeSpawnEggChance()
    {
        TwitchInteractions.worldChanges.SPAWN_EGG_CHANCE = (new Random()).nextInt(100);
    }

    /**
     * Replace a random item drop with another
     */
    private void changeLootDrop()
    {
        Item loot = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
        Item replacement = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        TwitchInteractions.worldChanges.REPLACE_LOOT.putString(loot.toString(), replacement.toString());
    }

    /**
     * Double or half the loot amount of a random item drop
     */
    private void changeLootAmount()
    {
        Item lootAmount = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        // Double or half the loot
        int amount = TwitchInteractions.worldChanges.LOOT_MODIFIER.getInt(lootAmount.toString());
        if (amount > 1) {
            amount = (new Random()).nextBoolean() ? amount * 2 : amount / 2;
        } else {
            amount = 2;
        }

        TwitchInteractions.worldChanges.LOOT_MODIFIER.putInt(lootAmount.toString(), amount);
    }

    /**
     * Replace the natural spawn of a mob with another
     */
    private void changeMobSpawn()
    {
        // Get a random mob to replace
        EntityType<?> entityType = ServerHelper.randomMobType();

        // Get a random mob to replace with
        EntityType<?> replacement = ServerHelper.randomMobType();

        TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.putString(entityType.toString(), replacement.getUntranslatedName());
    }
}
