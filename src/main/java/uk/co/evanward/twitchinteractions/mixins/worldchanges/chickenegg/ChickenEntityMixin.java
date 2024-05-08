package uk.co.evanward.twitchinteractions.mixins.worldchanges.chickenegg;

import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin
{
    @ModifyArg(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/ChickenEntity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;"))
    private ItemConvertible injected(ItemConvertible par1) {
        return TwitchInteractions.worldChanges.CHICKEN_EGG;
    }
}
