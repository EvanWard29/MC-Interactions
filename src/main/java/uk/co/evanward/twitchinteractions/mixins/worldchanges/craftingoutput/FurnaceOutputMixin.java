package uk.co.evanward.twitchinteractions.mixins.worldchanges.craftingoutput;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceOutputMixin
{
    @ModifyVariable(method = "craftRecipe", at = @At("STORE"), ordinal = 1)
    private static ItemStack changeAmount(ItemStack itemStack)
    {
        if (TwitchInteractions.worldChanges.RECIPE_MODIFIERS.contains(itemStack.getItem().toString())) {
            int amount = (int) Math.max(1, Math.ceil(1 * TwitchInteractions.worldChanges.RECIPE_MODIFIERS.getFloat(itemStack.getItem().toString())));

            itemStack.setCount(amount);
        }

        return itemStack;
    }

    @Redirect(method = "craftRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;increment(I)V"))
    private static void changeAmount(ItemStack itemStack, int amount)
    {
        if (TwitchInteractions.worldChanges.RECIPE_MODIFIERS.contains(itemStack.getItem().toString())) {
            amount = (int) Math.ceil(1 * TwitchInteractions.worldChanges.RECIPE_MODIFIERS.getFloat(itemStack.getItem().toString()));
        }

        itemStack.increment(amount);
    }
}
