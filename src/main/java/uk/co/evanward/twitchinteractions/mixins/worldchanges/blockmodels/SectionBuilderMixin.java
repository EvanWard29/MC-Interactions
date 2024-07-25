package uk.co.evanward.twitchinteractions.mixins.worldchanges.blockmodels;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(SectionBuilder.class)
public class SectionBuilderMixin
{
    @ModifyArg(method = "build", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V"))
    private BlockState replaceBlockModel(BlockState state)
    {
        if (TwitchInteractions.worldChanges.BLOCK_MODELS.contains(state.getBlock().asItem().toString())) {
            return Registries.BLOCK.get(Identifier.tryParse(TwitchInteractions.worldChanges.BLOCK_MODELS.getString(state.getBlock().asItem().toString()))).getDefaultState();
        }

        String blockItemName = state.getBlock().asItem().toString();
        blockItemName = blockItemName.substring(blockItemName.lastIndexOf(':') + 1);
        if (TwitchInteractions.worldChanges.BLOCK_MODELS.contains(blockItemName)) {
            return Registries.BLOCK.get(Identifier.ofVanilla(TwitchInteractions.worldChanges.BLOCK_MODELS.getString(blockItemName))).getDefaultState();
        }

        return state;
    }
}
