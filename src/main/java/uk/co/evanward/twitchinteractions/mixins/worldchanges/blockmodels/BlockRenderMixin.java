package uk.co.evanward.twitchinteractions.mixins.worldchanges.blockmodels;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(BlockRenderManager.class)
public class BlockRenderMixin
{
    @ModifyVariable(method = "renderBlockAsEntity", at = @At("HEAD"), argsOnly = true)
    private BlockState changeBlockModel(BlockState state)
    {
        String blockItem = state.getBlock().asItem().toString();
        blockItem = blockItem.substring(blockItem.lastIndexOf(':') + 1);

        if (TwitchInteractions.worldChanges.BLOCK_MODELS.contains(blockItem)) {
            state = Registries.BLOCK.get(Identifier.ofVanilla(TwitchInteractions.worldChanges.BLOCK_MODELS.getString(blockItem))).getDefaultState();
        }

        return state;
    }
}
