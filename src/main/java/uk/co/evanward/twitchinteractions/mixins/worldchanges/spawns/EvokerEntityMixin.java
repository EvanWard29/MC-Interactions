package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(targets = "net/minecraft/entity/mob/EvokerEntity$SummonVexGoal")
public class EvokerEntityMixin
{
    @ModifyArg(method = "castSpell", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
    private Entity replaceVex(Entity vex)
    {
        if (TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(vex.getType().toString())) {
            Entity replacement = Registries.ENTITY_TYPE.get(Identifier.tryParse(TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(vex.getType().toString())))
                .create(vex.getEntityWorld());

            replacement.setPosition(vex.getPos());

            vex = replacement;
        }

        return vex;
    }
}
