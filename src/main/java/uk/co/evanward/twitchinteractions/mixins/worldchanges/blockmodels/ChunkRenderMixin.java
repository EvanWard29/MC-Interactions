package uk.co.evanward.twitchinteractions.mixins.worldchanges.blockmodels;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.co.evanward.twitchinteractions.TwitchInteractions;


@Mixin(targets = "net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk$RebuildTask")
public abstract class ChunkRenderMixin
{
    @ModifyVariable(method = "render", at = @At("STORE"))
    private BlockState replaceBlock(BlockState blockState)
    {
        if (TwitchInteractions.worldChanges.BLOCK_MODELS.contains(blockState.getBlock().asItem().toString())) {
            blockState = Registries.BLOCK.get(Identifier.of("minecraft", TwitchInteractions.worldChanges.BLOCK_MODELS.getString(blockState.getBlock().asItem().toString()))).getDefaultState();
        }

        return blockState;
    }
}
