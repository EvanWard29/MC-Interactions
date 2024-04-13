package uk.co.evanward.twitchinteractions.config;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
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
    public NbtCompound DAMAGE_MODIFIERS;
    public int SPAWN_EGG_CHANCE;

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
        this.DAMAGE_MODIFIERS = new NbtCompound();
        this.SPAWN_EGG_CHANCE = 0;
    }

    /**
     * Write the world changes to NBT for saving
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putString("CHICKEN_EGG", CHICKEN_EGG.toString());
        nbt.putInt("DAY_LENGTH", DAY_LENGTH);
        nbt.putString("SHEEP_COLOUR", SHEEP_COLOUR.toString());
        nbt.putInt("ITEM_DESPAWN", ITEM_DESPAWN);
        nbt.put("DAMAGE_MODIFIERS", DAMAGE_MODIFIERS);
        nbt.putInt("SPAWN_EGG_CHANCE", SPAWN_EGG_CHANCE);

        return nbt;
    }

    /**
     * Load the existing world changes to NBT
     */
    public static WorldChanges createFromNbt(NbtCompound nbt)
    {
        WorldChanges worldChanges = new WorldChanges();

        worldChanges.CHICKEN_EGG = ServerHelper.getServer()
            .getRegistryManager()
            .get(RegistryKeys.ITEM)
            .get(Identifier.of("minecraft", nbt.getString("CHICKEN_EGG")));

        worldChanges.DAY_LENGTH = nbt.getInt("DAY_LENGTH");

        worldChanges.SHEEP_COLOUR = DyeColor.byName(nbt.getString("SHEEP_COLOUR"), DyeColor.WHITE);

        worldChanges.ITEM_DESPAWN = nbt.getInt("ITEM_DESPAWN");

        worldChanges.DAMAGE_MODIFIERS = nbt.getCompound("DAMAGE_MODIFIERS");

        worldChanges.SPAWN_EGG_CHANCE = nbt.getInt("SPAWN_EGG_CHANCE");

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
