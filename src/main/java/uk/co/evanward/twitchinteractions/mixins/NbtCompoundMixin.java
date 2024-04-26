package uk.co.evanward.twitchinteractions.mixins;

import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import uk.co.evanward.twitchinteractions.interfaces.CanBeDirty;

@Mixin(NbtCompound.class)
public abstract class NbtCompoundMixin implements CanBeDirty
{
    @Unique
    private boolean dirty = false;

    @Override
    public boolean isDirty()
    {
        return this.dirty;
    }

    @Override
    public void markDirty()
    {
        this.dirty = true;
    }

    @Override
    public void clean()
    {
        this.dirty = false;
    }
}
