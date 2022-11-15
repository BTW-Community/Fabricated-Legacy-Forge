package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.src.*;
import modloader.ModLoader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))//@At(value = "NEW", target = "net/minecraft/src/IntegratedServerListenThread", ordinal = 0))
    private void modLoaderRegisterCommands(Minecraft par1Minecraft, String par2Str, String par3Str, WorldSettings par4WorldSettings, CallbackInfo ci) {
        ModLoader.registerServer((IntegratedServer) (Object) this);
    }
}
