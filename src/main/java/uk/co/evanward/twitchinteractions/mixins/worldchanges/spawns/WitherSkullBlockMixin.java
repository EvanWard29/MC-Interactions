package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.block.WitherSkullBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(WitherSkullBlock.class)
public class WitherSkullBlockMixin
{
    @ModifyArg(
        method = "onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SkullBlockEntity;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z")
    )
    private static Entity replaceWither(Entity entity)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(EntityType.WITHER.toString())) {
            entity = Registries.ENTITY_TYPE.get(Identifier.of(
                "minecraft",
                TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(EntityType.WITHER.toString())
            )).create(entity.getServer().getWorld(
                    entity.getWorld().getRegistryKey()),
                replacementEntity -> {},
                entity.getBlockPos(),
                SpawnReason.SPAWNER,
                false,
                false
            );
        }

        return entity;
    }
}
