package uk.co.evanward.twitchinteractions.mixins.worldchanges;

import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin
{
    @ModifyVariable(method = "serverTick", at = @At("STORE"))
    private Entity replaceEntity(Entity entity)
    {
        // Replace spawner entities with their replacement
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entity.getType().toString())) {
            entity = Registries.ENTITY_TYPE.get(Identifier.of(
                "minecraft",
                TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(entity.getType().toString())
            )).create(entity.getServer().getWorld(
                entity.getWorld().getRegistryKey()),
                slimeEntity -> {},
                entity.getBlockPos(),
                SpawnReason.SPAWNER,
                false,
                false
            );
        }

        return entity;
    }
}
