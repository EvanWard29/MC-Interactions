package uk.co.evanward.twitchinteractions.mixins.worldchanges.daylength;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin
{
    @ModifyArg(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"))
    public long getDayTime(long timeOfDay)
    {
        return ((ClientWorld)(Object)this).getLevelProperties().getTimeOfDay() + (long) 24000 / TwitchInteractions.worldChanges.DAY_LENGTH;
    }
}
