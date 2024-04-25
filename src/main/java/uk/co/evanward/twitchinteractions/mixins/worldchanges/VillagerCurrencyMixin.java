package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(TradeOffer.class)
public class VillagerCurrencyMixin
{
    @ModifyVariable(method = "<init>(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;IIIFI)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static ItemStack changeBuyItem(ItemStack item)
    {
        if (item.isOf(Items.EMERALD)) {
            item = TwitchInteractions.worldChanges.VILLAGER_CURRENCY.getDefaultStack();
        }

        return item;
    }

    @ModifyVariable(method = "<init>(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;IIIFI)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private static ItemStack changeSellItem(ItemStack item)
    {
        if (item.isOf(Items.EMERALD)) {
            item = TwitchInteractions.worldChanges.VILLAGER_CURRENCY.getDefaultStack();
        }

        return item;
    }
}
