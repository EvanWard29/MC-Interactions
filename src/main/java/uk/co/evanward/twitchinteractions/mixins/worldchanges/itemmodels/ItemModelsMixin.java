package uk.co.evanward.twitchinteractions.mixins.worldchanges.itemmodels;

import net.minecraft.client.render.item.ItemModels;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ItemModels.class)
public class ItemModelsMixin
{
    @ModifyVariable(method = "getModel(Lnet/minecraft/item/Item;)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), argsOnly = true)
    private Item getModel(Item item)
    {
        if (TwitchInteractions.worldChanges.ITEM_MODELS.contains(item.toString())) {
            item = Registries.ITEM.get(Identifier.of("minecraft", TwitchInteractions.worldChanges.ITEM_MODELS.getString(item.toString())));
        }

        return item;
    }
}
