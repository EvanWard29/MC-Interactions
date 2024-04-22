package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(SpawnHelper.class)
public abstract class NaturalSpawnMixin
{
    @ModifyArg(
        method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;createMob(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/EntityType;)Lnet/minecraft/entity/mob/MobEntity;")
    )
    private static EntityType<?> replaceMobType(EntityType<?> entityType)
    {
        // Replace the spawning entity with its replacement
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entityType.toString())) {
            entityType = Registries.ENTITY_TYPE.get(Identifier.of(
                "minecraft",
                TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(entityType.toString())
            ));
        }

        return entityType;
    }
}
