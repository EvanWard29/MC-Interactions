package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.structure.WoodlandMansionGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

@Mixin(WoodlandMansionGenerator.Piece.class)
public class PieceMixin
{
    @ModifyArg(method = "handleMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ServerWorldAccess;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
    private Entity replaceMansionEntity(Entity entity)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entity.getType().toString())) {
            MobEntity replacement = (MobEntity) ServerHelper.getEntityReplacement(entity);
            replacement.setPersistent();

            entity = replacement;
        }

        return entity;
    }
}
