package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(SculkShriekerBlockEntity.class)
public class SculkShriekerBlockEntityMixin
{
    @ModifyArg(method = "trySpawnWarden", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LargeEntitySpawnHelper;trySpawnAt(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;IIILnet/minecraft/entity/LargeEntitySpawnHelper$Requirements;)Ljava/util/Optional;"))
    private EntityType<?> replaceWarden(EntityType<?> type)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(type.toString())) {
            type = Registries.ENTITY_TYPE.get(Identifier.tryParse(TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(type.toString())));
        }

        return type;
    }
}
