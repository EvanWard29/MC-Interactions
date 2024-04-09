package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(ItemEntity.class)
public class ItemDespawnTimeMixin
{
    @ModifyConstant(method = {"tick", "canMerge()Z"}, constant = @Constant(intValue = 6000))
    private int itemDespawnTime(int despawnTime)
    {
        return TwitchInteractions.worldChanges.ITEM_DESPAWN;
    }
}
