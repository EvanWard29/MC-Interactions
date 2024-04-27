package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ServerWorld.class)
public abstract class ServerWorldLengthOfDayMixin
{
    @ModifyArg(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
    public long getDayTime(long timeOfDay)
    {
        return ((ServerWorld)(Object)this).getLevelProperties().getTimeOfDay() + (long)  24000 / TwitchInteractions.worldChanges.DAY_LENGTH;
    }
}
