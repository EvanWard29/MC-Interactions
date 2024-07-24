package uk.co.evanward.twitchinteractions.mixins.worldchanges.blockmodels;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin
{
    @ModifyArg(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockModels;getModel(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BakedModel;"))
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
