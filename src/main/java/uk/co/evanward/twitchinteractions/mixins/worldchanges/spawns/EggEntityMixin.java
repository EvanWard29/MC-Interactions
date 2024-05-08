package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

@Mixin(EggEntity.class)
public class EggEntityMixin
{
    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private Entity replaceChicken(Entity entity)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entity.getType().toString())) {
            entity = ServerHelper.getEntityReplacement(entity);
        }

        return entity;
    }
}
