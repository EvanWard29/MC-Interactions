package uk.co.evanward.twitchinteractions.overrides;

import net.minecraft.nbt.NbtCompound;

public class BlockModelNbt extends NbtCompound
{
    private boolean dirty;

    public void markDirty()
    {
        this.dirty = true;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void clean()
    {
        this.dirty = false;
    }

    /**
     * Cast NbtCompound to appropriate class
     */
    public static BlockModelNbt fromNbtCompound(NbtCompound nbt)
    {
        BlockModelNbt blockModelNbt = new BlockModelNbt();

        for (String key : nbt.getKeys()) {
            blockModelNbt.put(key, nbt.get(key));
        }

        return blockModelNbt;
    }
}
