package uk.co.evanward.twitchinteractions.mixins.worldchanges.stacksize;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    @Shadow public abstract Item getItem();

    @Inject(method = "getMaxCount", at = @At("RETURN"), cancellable = true)
    private void modifyMaxCount(final CallbackInfoReturnable<Integer> cir)
    {
        if (TwitchInteractions.worldChanges != null && TwitchInteractions.worldChanges.STACK_SIZE.contains(this.getItem().toString())) {
            cir.setReturnValue(TwitchInteractions.worldChanges.STACK_SIZE.getCompound(this.getItem().toString()).getInt("current"));
        }
    }
}
