package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.interfaces.CanBeDirty;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class WorldRandomiseRedemption implements ChannelPoint.ChannelPointInterface
{
    /**
     * Change the following of the world:
     * <ul>
     *     <li>Item laid by chickens</li>
     *     <li>Natural mob spawns</li>
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

        this.changeSpawnEggChance();

        this.changeLootDrop();

        this.changeLootAmount();

        this.changeMobSpawn();

        this.changeStackSize();

        this.changeRecipeOutput();

        this.changeVillagerCurrency();

        this.changeSound();

        this.changeItemModel();

        this.changeBlockModel();

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

    /**
     * Double or half the max stack size of a random item
     */
    private void changeStackSize()
    {
        Item item = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        NbtCompound itemSizes;
        if (TwitchInteractions.worldChanges.STACK_SIZE.contains(item.toString())) {
            // Get the existing item size changes
            itemSizes = TwitchInteractions.worldChanges.STACK_SIZE.getCompound(item.toString());
        } else {
            itemSizes = new NbtCompound();

            // Set the default stack sizes
            itemSizes.putInt("default", item.getMaxCount());
            itemSizes.putInt("current", item.getMaxCount());
        }

        float size = itemSizes.getInt("current");

        // Double or half the current size
        size = (new Random()).nextBoolean() ? size * 2 : size / 2;

        if (size < 1) {
            // Reset to default if less than 1
            size = itemSizes.getInt("default");
        }

        itemSizes.putInt("current", MathHelper.ceil(size));
        TwitchInteractions.worldChanges.STACK_SIZE.put(item.toString(), itemSizes);
    }

    /**
     * Double or half the recipe output of an item
     */
    private void changeRecipeOutput()
    {
        // Get a random item
        Item item = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        float modifier;
        if (TwitchInteractions.worldChanges.RECIPE_MODIFIERS.contains(item.toString())) {
            modifier = TwitchInteractions.worldChanges.RECIPE_MODIFIERS.getFloat(item.toString());
        } else {
            modifier = 1.0f;
        }

        if (modifier == 1.0f) {
            // Double the modifier
            modifier = modifier * 2;
        } else {
            // Double or half the modifier
            modifier = (new Random()).nextBoolean() ? modifier * 2 : modifier / 2;
        }

        TwitchInteractions.worldChanges.RECIPE_MODIFIERS.putFloat(item.toString(), modifier);
    }

    /**
     * Change the currency Villagers trade in
     */
    private void changeVillagerCurrency()
    {
        // Get a random item to switch currency for
        TwitchInteractions.worldChanges.VILLAGER_CURRENCY = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
    }

    /**
     * Replace a random sound to another
     */
    private void changeSound()
    {
        SoundEvent sound = Registries.SOUND_EVENT.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
        SoundEvent replacement = Registries.SOUND_EVENT.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        TwitchInteractions.worldChanges.SOUNDS.putString(sound.getId().toString(), replacement.getId().toString());
    }

    /**
     * Replace an Item model with another
     */
    private void changeItemModel()
    {
        Item item = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
        Item replacement = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        TwitchInteractions.worldChanges.ITEM_MODELS.putString(item.toString(), replacement.toString());
    }

    /**
     * Replace a random block model with another
     */
    private void changeBlockModel()
    {
        String blockName;
        do {
            Block block = Registries.BLOCK.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
            blockName = block.getTranslationKey().substring(block.getTranslationKey().lastIndexOf('.') + 1);
        } while (blockName.equals("air"));


        String replacementName;
        do {
            Block replacement = Registries.BLOCK.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
            replacementName = replacement.getTranslationKey().substring(replacement.getTranslationKey().lastIndexOf('.') + 1);
        } while (replacementName.equals("air"));

        TwitchInteractions.worldChanges.BLOCK_MODELS.putString(blockName, replacementName);
        ((CanBeDirty) TwitchInteractions.worldChanges.BLOCK_MODELS).markDirty();
    }
}
