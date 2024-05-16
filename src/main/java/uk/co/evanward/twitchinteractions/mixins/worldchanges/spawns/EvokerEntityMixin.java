package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

@Mixin(targets = "net/minecraft/entity/mob/EvokerEntity$SummonVexGoal")
public class EvokerEntityMixin
{
    @ModifyArg(method = "castSpell", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
    private Entity replaceVex(Entity vex)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(vex.getType().toString())) {
            vex = ServerHelper.getEntityReplacement(vex);
        }

        return vex;
    }
}
