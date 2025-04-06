package uk.co.evanward.twitchinteractions.mixins.worldchanges.spawns;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

import java.util.Optional;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin
{
    @Inject(method = "getEntity", at = @At("RETURN"), cancellable = true)
    private static void replaceStructureEntity(ServerWorldAccess world, NbtCompound nbt, CallbackInfoReturnable<Optional<Entity>> cir)
    {
        Optional<Entity> entity = cir.getReturnValue();

        if (entity.isPresent() && TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.contains(entity.get().getType().toString())) {
            cir.setReturnValue(Optional.ofNullable(Registries.ENTITY_TYPE.get(
                Identifier.tryParse(TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(entity.get().getType().toString()))
            ).create(world.toServerWorld(), SpawnReason.COMMAND)));
        }
    }
}
