package uk.co.evanward.twitchinteractions.config;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

public class WorldChanges extends PersistentState
{
    public Item CHICKEN_EGG;
    public int DAY_LENGTH;
    public DyeColor SHEEP_COLOUR;
    public int ITEM_DESPAWN;
    public int SPAWN_EGG_CHANCE;
    public NbtCompound REPLACE_LOOT;
    public NbtCompound LOOT_MODIFIER;
    public NbtCompound REPLACE_MOB_SPAWN;
    public NbtCompound STACK_SIZE;
    public NbtCompound RECIPE_MODIFIERS;
    public Item VILLAGER_CURRENCY;
    public NbtCompound SOUNDS;
    public NbtCompound ITEM_MODELS;
    public NbtCompound BLOCK_MODELS;
    public boolean CHARGED_CREEPERS;

    private static final Type<WorldChanges> type = new Type<>(
        WorldChanges::new,
        WorldChanges::createFromNbt,
        null
    );

    /**
     * Create the default world change values
     */
    WorldChanges()
    {
        this.CHICKEN_EGG = Items.EGG;
        this.DAY_LENGTH = 24000;
        this.SHEEP_COLOUR = DyeColor.WHITE;
        this.ITEM_DESPAWN = 6000;
        this.SPAWN_EGG_CHANCE = 0;
        this.REPLACE_LOOT = new NbtCompound();
        this.LOOT_MODIFIER = new NbtCompound();
        this.REPLACE_MOB_SPAWN = new NbtCompound();
        this.STACK_SIZE = new NbtCompound();
        this.RECIPE_MODIFIERS = new NbtCompound();
        this.VILLAGER_CURRENCY = Items.EMERALD;
        this.SOUNDS = new NbtCompound();
        this.ITEM_MODELS = new NbtCompound();
        this.BLOCK_MODELS = new NbtCompound();
        this.CHARGED_CREEPERS = false;
    }

    /**
     * Write the world changes to NBT for saving
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        nbt.putString("CHICKEN_EGG", CHICKEN_EGG.toString());
        nbt.putInt("DAY_LENGTH", DAY_LENGTH);
        nbt.putString("SHEEP_COLOUR", SHEEP_COLOUR.toString());
        nbt.putInt("ITEM_DESPAWN", ITEM_DESPAWN);
        nbt.putInt("SPAWN_EGG_CHANCE", SPAWN_EGG_CHANCE);
        nbt.put("REPLACE_LOOT", REPLACE_LOOT);
        nbt.put("LOOT_MODIFIER", LOOT_MODIFIER);
        nbt.put("REPLACE_MOB_SPAWN", REPLACE_MOB_SPAWN);
        nbt.put("STACK_SIZE", STACK_SIZE);
        nbt.put("RECIPE_MODIFIERS", RECIPE_MODIFIERS);
        nbt.putString("VILLAGER_CURRENCY", VILLAGER_CURRENCY.toString());
        nbt.put("SOUNDS", SOUNDS);
        nbt.put("ITEM_MODELS", ITEM_MODELS);
        nbt.put("BLOCK_MODELS", BLOCK_MODELS);
        nbt.putBoolean("CHARGED_CREEPERS", CHARGED_CREEPERS);

        return nbt;
    }

    /**
     * Load the existing world changes to NBT
     */
    public static WorldChanges createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        WorldChanges worldChanges = new WorldChanges();

        worldChanges.CHICKEN_EGG = ServerHelper.getServer()
            .getRegistryManager()
            .get(RegistryKeys.ITEM)
            .get(Identifier.of("minecraft", nbt.getString("CHICKEN_EGG")));

        worldChanges.DAY_LENGTH = nbt.getInt("DAY_LENGTH");

        worldChanges.SHEEP_COLOUR = DyeColor.byName(nbt.getString("SHEEP_COLOUR"), DyeColor.WHITE);

        worldChanges.ITEM_DESPAWN = nbt.getInt("ITEM_DESPAWN");

        worldChanges.SPAWN_EGG_CHANCE = nbt.getInt("SPAWN_EGG_CHANCE");

        worldChanges.REPLACE_LOOT = nbt.getCompound("REPLACE_LOOT");

        worldChanges.LOOT_MODIFIER = nbt.getCompound("LOOT_MODIFIER");

        worldChanges.REPLACE_MOB_SPAWN = nbt.getCompound("REPLACE_MOB_SPAWN");

        worldChanges.STACK_SIZE = nbt.getCompound("STACK_SIZE");

        worldChanges.RECIPE_MODIFIERS = nbt.getCompound("RECIPE_MODIFIERS");

        worldChanges.VILLAGER_CURRENCY = ServerHelper.getServer()
            .getRegistryManager()
            .get(RegistryKeys.ITEM)
            .get(Identifier.of("minecraft", nbt.getString("VILLAGER_CURRENCY")));

        worldChanges.SOUNDS = nbt.getCompound("SOUNDS");

        worldChanges.ITEM_MODELS = nbt.getCompound("ITEM_MODELS");

        worldChanges.BLOCK_MODELS = nbt.getCompound("BLOCK_MODELS");

        worldChanges.CHARGED_CREEPERS = nbt.getBoolean("CHARGED_CREEPERS");

        return worldChanges;
    }

    /**
     * Get or create world changes on server start
     */
    public static WorldChanges getServerState(MinecraftServer server)
    {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        WorldChanges worldChanges = persistentStateManager.getOrCreate(type, "world_changes");

        // Mark as dirty to tell Minecraft to save on exit
        worldChanges.markDirty();

        return worldChanges;
    }
}
