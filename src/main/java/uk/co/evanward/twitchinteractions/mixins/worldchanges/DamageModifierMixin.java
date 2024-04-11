package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ServerPlayerEntity.class)
public abstract class DamageModifierMixin
{
    @Unique
    private DamageSource damageSource;

    @Inject(method = "damage", at = @At(value = "HEAD"))
    private void getDamageSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        this.damageSource = source;
    }

    @ModifyVariable(method = "damage", at = @At(value = "HEAD"), argsOnly = true)
    protected float damageModifier(float amount)
    {
        return amount * TwitchInteractions.worldChanges.DAMAGE_MODIFIERS.getFloat(damageSource.getType().msgId());
    }
}
