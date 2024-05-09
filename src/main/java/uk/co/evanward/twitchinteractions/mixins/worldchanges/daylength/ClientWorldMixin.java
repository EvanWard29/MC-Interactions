package uk.co.evanward.twitchinteractions.mixins.worldchanges.daylength;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin
{
    @Shadow public abstract ClientWorld.Properties getLevelProperties();

    @ModifyArg(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"))
    public long setTimeOfDay(long timeOfDay)
    {
        return this.getLevelProperties().getTimeOfDay() + delta();
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
