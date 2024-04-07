package uk.co.evanward.twitchinteractions.config;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

public class WorldChanges extends PersistentState
{
    public Item CHICKEN_EGG;

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
    }

    /**
     * Write the world changes to NBT for saving
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putString("CHICKEN_EGG", CHICKEN_EGG.toString());

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

        TwitchInteractions.logger.info("CHICKEN_EGG: " + worldChanges.CHICKEN_EGG.toString());
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
