package uk.co.evanward.twitchinteractions.mixins.worldchanges.craftingoutput;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ShapedRecipe.class)
public abstract class ShapedCraftingOutputMixin
{
    @Inject(method = "craft(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    public void changeOutput(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager, CallbackInfoReturnable<ItemStack> cir)
    {
        ItemStack itemStack = cir.getReturnValue();

        if (TwitchInteractions.worldChanges.RECIPE_MODIFIERS.contains(itemStack.getItem().toString())) {
            float modifier = TwitchInteractions.worldChanges.RECIPE_MODIFIERS.getFloat(itemStack.getItem().toString());
            itemStack.setCount((int) (itemStack.getCount() * modifier));
        }
    }
}
