package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin
{
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    public Entity replaceTurtle(Entity entity)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entity.getType().toString())) {
            entity = ServerHelper.getEntityReplacement(entity);
        }

        return entity;
    }
}
