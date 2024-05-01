package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.spawner.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(PatrolSpawner.class)
public class PatrolSpawnerMixin
{
    @ModifyArg(method = "spawnPillager", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
    private Entity replacePillager(Entity pillager)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(pillager.getType().toString())) {
            Entity entity = Registries.ENTITY_TYPE.get(Identifier.tryParse(TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(pillager.getType().toString())))
                .create(pillager.getWorld());

            entity.setPosition(pillager.getPos());
            pillager = entity;
        }

        return  pillager;
    }
}
