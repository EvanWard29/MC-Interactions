package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin
{
    @Shadow @Final private static TrackedData<Boolean> CHARGED;

    @Unique
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData)
    {
        if (TwitchInteractions.worldChanges.CHARGED_CREEPERS) {
            ((CreeperEntity)(Object)this).getDataTracker().set(CHARGED, true);
        }

        return entityData;
    }
}
