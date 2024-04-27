package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(TradeOffer.class)
public class VillagerCurrencyMixin
{
    @ModifyVariable(method = "<init>(Lnet/minecraft/village/TradedItem;Ljava/util/Optional;Lnet/minecraft/item/ItemStack;IIZIIFI)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static TradedItem changeBuyItem(TradedItem buyItem)
    {
        if (buyItem.itemStack().isOf(Items.EMERALD)) {
            buyItem = new TradedItem(TwitchInteractions.worldChanges.VILLAGER_CURRENCY);
        }

        return buyItem;
    }

    @ModifyVariable(method = "<init>(Lnet/minecraft/village/TradedItem;Ljava/util/Optional;Lnet/minecraft/item/ItemStack;IIZIIFI)V", at = @At("HEAD"), argsOnly = true)
    private static ItemStack changeSellItem(ItemStack sellItem)
    {
        if (sellItem.isOf(Items.EMERALD)) {
            sellItem = TwitchInteractions.worldChanges.VILLAGER_CURRENCY.getDefaultStack();
        }

        return sellItem;
    }
}
