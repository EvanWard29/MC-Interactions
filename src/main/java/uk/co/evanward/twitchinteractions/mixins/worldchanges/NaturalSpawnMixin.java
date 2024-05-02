package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.entity.Entity;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

@Mixin(SpawnHelper.class)
public abstract class NaturalSpawnMixin
{
    @ModifyArg(
        method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V")
    )
    private static Entity replaceEntityOnNaturalSpawn(Entity entity)
    {
        // Replace the spawning entity with its replacement
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entity.getType().toString())) {
            entity = ServerHelper.getEntityReplacement(entity);
        }

        return entity;
    }

    @ModifyArg(method = "populateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ServerWorldAccess;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
    private static Entity replaceEntityOnChunkGeneration(Entity entity)
    {
        // Replace the spawning entity with its replacement
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entity.getType().toString())) {
            entity = ServerHelper.getEntityReplacement(entity);
        }

        return entity;
    }
}
