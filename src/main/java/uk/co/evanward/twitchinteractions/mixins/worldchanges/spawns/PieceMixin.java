package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(WoodlandMansionGenerator.Piece.class)
public class PieceMixin
{
    @ModifyArg(method = "handleMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ServerWorldAccess;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
    private Entity replaceMansionEntity(Entity entity)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entity.getType().toString())) {
            MobEntity replacement = (MobEntity) Registries.ENTITY_TYPE.get(Identifier.tryParse(TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(entity.getType().toString())))
                .create(entity.getEntityWorld());

            replacement.setPosition(entity.getPos());
            replacement.setPersistent();

            entity = replacement;
        }

        return entity;
    }
}
