package uk.co.evanward.twitchinteractions.mixins.worldchanges.craftingoutput;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(AbstractCookingRecipe.class)
public abstract class CookingOutputMixin
{
    @Inject(method = "craft", at = @At("RETURN"))
    public void changeOutputAmount(CallbackInfoReturnable<ItemStack> cir)
    {
        ItemStack itemStack = cir.getReturnValue();

        if (TwitchInteractions.worldChanges.RECIPE_MODIFIERS.contains(itemStack.getItem().toString())) {
            float modifier = TwitchInteractions.worldChanges.RECIPE_MODIFIERS.getFloat(itemStack.getItem().toString());
            itemStack.setCount((int) (itemStack.getCount() * modifier));

            TwitchInteractions.logger.info("AND HERE");
        }
    }
}
