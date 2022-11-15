package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import modloader.ModLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow public RenderEngine renderEngine;

    @Inject(method = "startGame", at = @At(value = "RETURN"))
    private void modLoaderAddAllRenderers(CallbackInfo ci) {
        ModLoader.addAllRenderers(((RenderManagerAccessor) RenderManager.instance).getEntityRenderMap());
        this.renderEngine.refreshTextures();
    }
}
