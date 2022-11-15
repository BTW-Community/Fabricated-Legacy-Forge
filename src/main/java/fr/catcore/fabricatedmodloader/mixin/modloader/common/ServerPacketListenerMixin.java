package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import modloader.ModLoader;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(NetServerHandler.class)
public class ServerPacketListenerMixin {

    @Inject(method = "handleChat", at = @At("HEAD"))
    private void modLoaderServerChat(Packet3Chat par1, CallbackInfo ci) {
        ModLoader.serverChat((NetServerHandler) (Object) this, par1.message);
    }

    @Inject(method = "handleCustomPayload", at = @At("RETURN"))
    private void modLoaderServerCustomPayload(Packet250CustomPayload par1, CallbackInfo ci) {
        if (!Objects.equals(par1.channel, "MC|BEdit")
                && !Objects.equals(par1.channel, "MC|BSign")
                && !Objects.equals(par1.channel, "MC|TrSel")
                && !Objects.equals(par1.channel, "MC|AdvCdm")
                && !Objects.equals(par1.channel, "MC|Beacon")
                && !Objects.equals(par1.channel, "MC|ItemName")) {
            ModLoader.serverCustomPayload((NetServerHandler) (Object) this, par1);
        }
    }
}
