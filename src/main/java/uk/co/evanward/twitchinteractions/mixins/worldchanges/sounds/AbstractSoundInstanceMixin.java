package uk.co.evanward.twitchinteractions.mixins.worldchanges.sounds;

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin
{
    @ModifyVariable(method = "<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/sound/SoundCategory;Lnet/minecraft/util/math/random/Random;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static Identifier replaceSound(Identifier soundId)
    {
        // Avoid crashing on client load
        if (TwitchInteractions.worldChanges == null) {
            return soundId;
        }

        if (TwitchInteractions.worldChanges.SOUNDS.contains(soundId.toString())) {
            soundId = Identifier.tryParse(TwitchInteractions.worldChanges.SOUNDS.getString(soundId.toString()));
        }

        return soundId;
    }
}
