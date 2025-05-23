package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
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
     *     <li>Dropped loot</li>
     *     <li>Double or half the max item stack size</li>
     *     <li>Replace sound with another</li>
     *     <li>Villager currency</li>
     *     <li>Length of day</li>
     *     <li>Sheep colour</li>
     *     <li>Loot double or half</li>
     *     <li>Recipe output double or half</li>
     *     <li>Spawn egg chance</li>
     *     <li>Item despawn time</li>
     *     <li>Item models</li>
     *     <li>Block models</li>
     * </ul>
     */
    @Override
    public void trigger(JSONObject event)
    {
        AnnouncementHelper.playAnnouncement(event.getString("user_name"), "Randomised The World!");

        sendMessage(Text.literal("-----------------"));

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
            .getOrThrow(RegistryKeys.ITEM)
            .getRandom(ServerHelper.getConnectedPlayer().getRandom())
            .get()
            .value();

        sendMessage(Text.literal("Chickens now lay ")
            .append(TwitchInteractions.worldChanges.CHICKEN_EGG.getName().copy().formatted(Formatting.AQUA)));
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

        sendMessage(Text.literal("The length of day is now ")
            .append(Text.literal(String.valueOf((float) TwitchInteractions.worldChanges.DAY_LENGTH / 1200)).formatted(Formatting.AQUA))
            .append(Text.literal(" minutes")).formatted(Formatting.GOLD));
    }

    /**
     * Change the colour of naturally spawning sheep
     */
    private void changeSheepColour()
    {
        TwitchInteractions.worldChanges.SHEEP_COLOUR = DyeColor.values()[new Random().nextInt(DyeColor.values().length)];

        sendMessage(Text.literal("Natural sheep are now ")
            .append(Text.literal(TwitchInteractions.worldChanges.SHEEP_COLOUR.getName()).formatted(Formatting.AQUA)));
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

        sendMessage(Text.literal("Item despawn time is now ")
            .append(Text.literal(String.valueOf((float) TwitchInteractions.worldChanges.ITEM_DESPAWN / 1200)).formatted(Formatting.AQUA))
            .append(Text.literal(" minutes")).formatted(Formatting.GOLD));
    }

    /**
     * Set the chance of a spawn egg dropping when killing a mob
     */
    private void changeSpawnEggChance()
    {
        TwitchInteractions.worldChanges.SPAWN_EGG_CHANCE = (new Random()).nextInt(100);

        sendMessage(Text.literal("The chance of a spawn egg dropping is now ")
            .append(Text.literal(TwitchInteractions.worldChanges.SPAWN_EGG_CHANCE + "%").formatted(Formatting.AQUA)));
    }

    /**
     * Replace a random item drop with another
     */
    private void changeLootDrop()
    {
        Item loot = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
        Item replacement = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        TwitchInteractions.worldChanges.REPLACE_LOOT.putString(loot.toString(), replacement.toString());

        sendMessage(Text.empty().append(loot.getName().copy().formatted(Formatting.AQUA))
            .append(Text.literal(" loot has been replaced with ").formatted(Formatting.GOLD))
            .append(replacement.getName().copy().formatted(Formatting.AQUA)).formatted(Formatting.GOLD));
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

        sendMessage(Text.empty().append(lootAmount.getName().copy().formatted(Formatting.AQUA))
            .append(Text.literal(" loot has been multiplied by "))
            .append(Text.literal(String.valueOf(amount)).formatted(Formatting.AQUA)));
    }

    /**
     * Replace the natural spawn of a mob with another
     */
    private void changeMobSpawn()
    {
        // Get a random mob to replace
        EntityType<?> entityType = ServerHelper.randomMobType();

        // Get a random mob to replace with
        EntityType<?> replacement = ServerHelper.randomMobTypeReplacement();

        TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.putString(entityType.toString(), replacement.getUntranslatedName());

        sendMessage(Text.literal("Spawns of ")
            .append(entityType.getName().copy().formatted(Formatting.AQUA))
            .append(Text.literal(" have been replaced with ").formatted(Formatting.GOLD))
            .append(replacement.getName().copy().formatted(Formatting.AQUA)).formatted(Formatting.GOLD));
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

        sendMessage(Text.literal("The stack size of ")
            .append(item.getName().copy().formatted(Formatting.AQUA)).append(" has been changed to ")
            .append(Text.literal(String.valueOf(itemSizes.getInt("current"))).formatted(Formatting.AQUA)));
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

        sendMessage(Text.literal("The recipe output of ").append(item.getName().copy().formatted(Formatting.AQUA))
            .append(Text.literal(" has been multiplied by ").formatted(Formatting.GOLD))
            .append(Text.literal(String.valueOf((int) modifier)).formatted(Formatting.AQUA)).formatted(Formatting.GOLD));
    }

    /**
     * Change the currency Villagers trade in
     */
    private void changeVillagerCurrency()
    {
        // Get a random item to switch currency for
        TwitchInteractions.worldChanges.VILLAGER_CURRENCY = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        sendMessage(Text.literal("Villager currency has been replaced with ")
            .append(TwitchInteractions.worldChanges.VILLAGER_CURRENCY.getName().copy().formatted(Formatting.AQUA)));
    }

    /**
     * Replace a random sound to another
     */
    private void changeSound()
    {
        SoundEvent sound = Registries.SOUND_EVENT.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
        SoundEvent replacement = Registries.SOUND_EVENT.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        TwitchInteractions.worldChanges.SOUNDS.putString(sound.id().toString(), replacement.id().toString());

        sendMessage(Text.literal("The sound ")
            .append(Text.literal(sound.id().getPath()).formatted(Formatting.AQUA))
            .append(Text.literal(" has been replaced with ").formatted(Formatting.GOLD))
            .append(Text.literal(replacement.id().getPath()).formatted(Formatting.AQUA)).formatted(Formatting.GOLD));
    }

    /**
     * Replace an Item model with another
     */
    private void changeItemModel()
    {
        Item item = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
        Item replacement = Registries.ITEM.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();

        TwitchInteractions.worldChanges.ITEM_MODELS.putString(item.toString(), replacement.toString());

        sendMessage(Text.literal("The item model of ")
            .append(item.getName().copy().formatted(Formatting.AQUA))
            .append(" has been replaced with ")
            .append(replacement.getName().copy().formatted(Formatting.AQUA)));
    }

    /**
     * Replace a random block model with another
     */
    private void changeBlockModel()
    {
        Block block = getRandomBlockModel();
        Block replacement = getRandomBlockModel();

        TwitchInteractions.worldChanges.BLOCK_MODELS.putString(
            block.getTranslationKey().substring(block.getTranslationKey().lastIndexOf('.') + 1),
            replacement.getTranslationKey().substring(replacement.getTranslationKey().lastIndexOf('.') + 1)
        );

        TwitchInteractions.worldChanges.BLOCK_MODELS.markDirty();

        sendMessage(Text.literal("The block model of ")
            .append(block.getName().copy().formatted(Formatting.AQUA))
            .append(Text.literal(" has been replaced with ").formatted(Formatting.GOLD))
            .append(replacement.getName().copy().formatted(Formatting.AQUA)).formatted(Formatting.GOLD));
    }

    /**
     * Send a message to the connected player describing the world change
     */
    private void sendMessage(Text message)
    {
        ServerHelper.getConnectedPlayer().sendMessage(message);
    }

    /**
     * Get a random block that is not:
     * <ul>
     *     <li>Air</li>
     *     <li>Shulker Box</li>
     *     <li>Redstone Wire</li>
     * </ul>
     */
    private Block getRandomBlockModel()
    {
        Block block;
        do {
            block = Registries.BLOCK.getRandom(ServerHelper.getConnectedPlayer().getRandom()).get().value();
        } while (block.getName().contains(Text.of("Air"))
            || block.getDefaultState().isIn(BlockTags.SHULKER_BOXES)
        );

        return block;
    }
}
