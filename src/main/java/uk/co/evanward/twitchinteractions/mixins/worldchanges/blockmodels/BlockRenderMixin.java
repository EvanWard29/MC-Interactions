package uk.co.evanward.twitchinteractions.mixins.worldchanges.blockmodels;

import net.minecraft.block.Block;
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
    private BlockState changeBlockModel(BlockState blockState)
    {
        Block block = blockState.getBlock();

        if (TwitchInteractions.worldChanges.BLOCK_MODELS.contains(block.asItem().toString())) {
            blockState = Registries.BLOCK.get(Identifier.of("minecraft", TwitchInteractions.worldChanges.BLOCK_MODELS.getString(block.asItem().toString()))).getDefaultState();
        }

        return blockState;
    }
}
