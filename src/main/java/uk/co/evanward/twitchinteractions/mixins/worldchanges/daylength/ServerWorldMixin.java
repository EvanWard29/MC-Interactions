package uk.co.evanward.twitchinteractions.mixins.worldchanges.daylength;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
{
    @ModifyArg(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
    public long getDayTime(long timeOfDay)
    {
        return ((ServerWorld)(Object)this).getLevelProperties().getTimeOfDay() + delta();
    }

    private int delta()
    {
        float f = 24000F / (float) TwitchInteractions.worldChanges.DAY_LENGTH;
        int i = MathHelper.floor(f);

        if (Random.create().nextFloat() < MathHelper.fractionalPart(f)) {
            i++;
        }

        return i;
    }
}
