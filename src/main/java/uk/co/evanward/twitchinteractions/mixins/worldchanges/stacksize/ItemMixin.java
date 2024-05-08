package uk.co.evanward.twitchinteractions.mixins.worldchanges.stacksize;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(Item.class)
public abstract class ItemMixin
{
    @Inject(method = "getMaxCount", at = @At("RETURN"), cancellable = true)
    private void getMaxCount(CallbackInfoReturnable<Integer> cir)
    {
        // Replace the max count with the modified value if set
        if (TwitchInteractions.worldChanges.STACK_SIZE.contains(this.toString())) {
            cir.setReturnValue(TwitchInteractions.worldChanges.STACK_SIZE.getCompound(this.toString()).getInt("current"));
        }
    }
}
