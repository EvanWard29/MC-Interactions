package uk.co.evanward.twitchinteractions.mixins.worldchanges.loot;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(LootTable.class)
public abstract class LootTableMixin
{
    @ModifyVariable(method = "method_331", at = @At(value = "HEAD"), argsOnly = true)
    private static ItemStack replaceItem(ItemStack stack)
    {
        // Replace the loot
        if (TwitchInteractions.worldChanges.REPLACE_LOOT.contains(stack.getItem().toString())) {
            stack = new ItemStack(Registries.ITEM.get(Identifier.tryParse(TwitchInteractions.worldChanges.REPLACE_LOOT.getString(stack.getItem().toString()))));
        }

        // Set the loot amount
        if (TwitchInteractions.worldChanges.LOOT_MODIFIER.contains(stack.getItem().toString())) {
            stack.setCount(TwitchInteractions.worldChanges.LOOT_MODIFIER.getInt(stack.getItem().toString()));
        }

        return stack;
    }
}
