package uk.co.evanward.twitchinteractions.mixins;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions.GambleRedemption;

@Mixin(CreeperEntity.class)
public abstract class CreeperMixin
{
    @Shadow @Final private static TrackedData<Boolean> CHARGED;

    @Unique
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt)
    {
        if (GambleRedemption.chargedCreepers) {
            ((CreeperEntity)(Object)this).getDataTracker().set(CHARGED, true);
        }

        return entityData;
    }
}
