package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.src.*;
import modloader.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureStitched.class)
public class class_1528Mixin {

    @Inject(method = "makeTextureStitched", cancellable = true, at = @At("HEAD"))
    private static void modLoaderGetCustomAnimationLogic(String string, CallbackInfoReturnable<TextureStitched> cir) {
        TextureStitched texture = ModLoader.getCustomAnimationLogic(string);
        if (texture != null) {
            cir.setReturnValue(texture);
        }
    }
}
