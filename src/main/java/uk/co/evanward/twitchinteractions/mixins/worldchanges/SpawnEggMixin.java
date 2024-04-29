package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

import java.util.Random;

@Mixin(LivingEntity.class)
public class SpawnEggMixin
{
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "drop", at = @At(value = "TAIL"))
    private void dropEgg(DamageSource source, CallbackInfo ci)
    {
        int spawnEggChance = TwitchInteractions.worldChanges.SPAWN_EGG_CHANCE;
        if (spawnEggChance > 0 && (new Random()).nextInt(100) <= spawnEggChance) {
            Item spawnEgg = SpawnEggItem.forEntity(((LivingEntity)(Object)this).getType());

            // Replace the spawn egg with the replaced loot
            if (TwitchInteractions.worldChanges.REPLACE_LOOT.contains(spawnEgg.toString())) {
                spawnEgg = Registries.ITEM.get(Identifier.tryParse(TwitchInteractions.worldChanges.REPLACE_LOOT.getString(spawnEgg.toString())));
            }

            if (spawnEgg != null) {
                // Take into account the loot modifier
                ItemStack spawnEggStack = new ItemStack(spawnEgg);
                spawnEggStack.setCount(Math.max(1, TwitchInteractions.worldChanges.LOOT_MODIFIER.getInt(spawnEgg.toString())));

                ((LivingEntity)(Object)this).dropStack(spawnEggStack);
            }
        }
    }
}
