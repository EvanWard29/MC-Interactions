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
        String blockItem = state.getBlock().asItem().toString();
        blockItem = blockItem.substring(blockItem.lastIndexOf(':') + 1);

        if (TwitchInteractions.worldChanges.BLOCK_MODELS.contains(blockItem)) {
             state = Registries.BLOCK.get(Identifier.ofVanilla(TwitchInteractions.worldChanges.BLOCK_MODELS.getString(blockItem))).getDefaultState();
        }

        return state;
    }
}
