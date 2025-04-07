package uk.co.evanward.twitchinteractions.mixins.worldchanges.craftingoutput;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SingleStackRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(SingleStackRecipe.class)
public class SingleStackRecipeMixin
{
    @Inject(method = "craft(Lnet/minecraft/recipe/input/SingleStackRecipeInput;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void injected(SingleStackRecipeInput singleStackRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup, CallbackInfoReturnable<ItemStack> cir)
    {
        ItemStack original = cir.getReturnValue();

        if (TwitchInteractions.worldChanges.RECIPE_MODIFIERS.contains(original.getItem().toString())) {
            float modifier = TwitchInteractions.worldChanges.RECIPE_MODIFIERS.getFloat(original.getItem().toString());
            original.setCount((int) (original.getCount() * modifier));
        }

        cir.setReturnValue(original);
    }
}
