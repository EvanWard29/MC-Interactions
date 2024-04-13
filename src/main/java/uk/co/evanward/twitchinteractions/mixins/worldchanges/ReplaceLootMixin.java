package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(LootTable.class)
public abstract class ReplaceLootMixin
{
    @ModifyVariable(method = "method_331", at = @At(value = "HEAD"), argsOnly = true)
    private static ItemStack replaceItem(ItemStack stack)
    {
        if (TwitchInteractions.worldChanges.REPLACE_LOOT.contains(stack.getItem().toString())) {
            return new ItemStack(Registries.ITEM.get(Identifier.of("minecraft", TwitchInteractions.worldChanges.REPLACE_LOOT.getString(stack.getItem().toString()))));
        } else {
            return stack;
        }
    }
}
