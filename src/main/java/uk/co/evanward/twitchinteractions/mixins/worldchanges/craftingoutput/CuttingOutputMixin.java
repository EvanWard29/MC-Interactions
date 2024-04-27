package uk.co.evanward.twitchinteractions.mixins.worldchanges.craftingoutput;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(CuttingRecipe.class)
public abstract class CuttingOutputMixin
{
    @Inject(method = "craft", at = @At("RETURN"))
    public void changeOutputAmount(Inventory inventory, RegistryWrapper.WrapperLookup lookup, CallbackInfoReturnable<ItemStack> cir)
    {
        ItemStack itemStack = cir.getReturnValue();

        if (TwitchInteractions.worldChanges.RECIPE_MODIFIERS.contains(itemStack.getItem().toString())) {
            float modifier = TwitchInteractions.worldChanges.RECIPE_MODIFIERS.getFloat(itemStack.getItem().toString());
            itemStack.setCount((int) (itemStack.getCount() * modifier));
        }
    }
}
