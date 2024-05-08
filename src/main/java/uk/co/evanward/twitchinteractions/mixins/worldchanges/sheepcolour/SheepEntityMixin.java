package uk.co.evanward.twitchinteractions.mixins.worldchanges.sheepcolour;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(SheepEntity.class)
public class SheepEntityMixin
{
    @ModifyArg(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;setColor(Lnet/minecraft/util/DyeColor;)V"))
    public DyeColor getSheepColour(DyeColor color)
    {
        return TwitchInteractions.worldChanges.SHEEP_COLOUR;
    }
}
