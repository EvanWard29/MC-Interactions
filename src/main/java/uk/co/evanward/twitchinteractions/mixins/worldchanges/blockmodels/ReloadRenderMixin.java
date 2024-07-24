package uk.co.evanward.twitchinteractions.mixins.worldchanges.blockmodels;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(GameRenderer.class)
public class ReloadRenderMixin
{
    @Shadow @Final MinecraftClient client;

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void markChanged(RenderTickCounter tickCounter, CallbackInfo ci)
    {
        // Re-render models if new models have been replaced
        if (TwitchInteractions.worldChanges.BLOCK_MODELS.isDirty()) {
            client.worldRenderer.reload();
            TwitchInteractions.worldChanges.BLOCK_MODELS.clean();
        }
    }
}
