package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.src.*;
import com.google.common.collect.Lists;
import fr.catcore.fabricatedmodloader.utils.class_535Data;
import modloader.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RenderBlocks.class)
public abstract class class_535Mixin {

    @Shadow
    public IBlockAccess blockAccess;

    @Shadow
    public static boolean fancyGrass;
    private static final List<Integer> RENDER_BLOCKS = Lists.newArrayList(
            -1, 0, 4, 31, 1, 2, 20, 11, 39,
            5, 13, 9, 19, 23, 6, 3, 8, 7, 10, 27,
            32, 12, 29, 30, 14, 15, 36, 37, 16, 17,
            18, 21, 24, 33, 35, 25, 26, 28, 34, 38
    );

    @Inject(method = "renderBlockByRenderType", at = @At("RETURN"), cancellable = true)
    private void modLoaderRenderWorldBlock(Block block, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        int type = block.getRenderType();
        if (!cir.getReturnValue() && !RENDER_BLOCKS.contains(type)) {
            cir.setReturnValue(ModLoader.renderWorldBlock((RenderBlocks) (Object) this, this.blockAccess, i, j, k, block, type));
        }
    }

    private static final List<Integer> RENDER_BLOCKS_INV = Lists.newArrayList(
            1, 19, 23, 13, 22, 6, 2, 10, 27, 11, 21, 32, 35, 34, 38
    );

    @Inject(method = "renderBlockAsItemVanilla",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Block;getRenderType()I", ordinal = 0, remap = true),
            cancellable = true, remap = false
    )
    private void modLoaderRenderInvBlock(Block block, int i, float f, CallbackInfo ci) {
        int var6 = block.getRenderType();
        if (var6 != 0 && var6 != 31 && var6 != 39 && var6 != 16 && var6 != 26) {
            if (!RENDER_BLOCKS_INV.contains(var6)) {
                ModLoader.renderInvBlock((RenderBlocks) (Object) this, block, i, var6);
                ci.cancel();
            }
        }
    }

    @Inject(method = "doesRenderIDRenderItemIn3D", at = @At("RETURN"), cancellable = true, remap = false)
    private static void modLoaderRenderBlockIsItemFull3D(int i, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            cir.setReturnValue(ModLoader.renderBlockIsItemFull3D(i));
        }
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void modLoaderCfgGrassFix(CallbackInfo ci) {
        fancyGrass = class_535Data.cfgGrassFix;
    }
}
