package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import com.google.common.collect.Lists;
import fr.catcore.fabricatedmodloader.mixin.modloader.common.BlockEntityAccessor;
import modloader.EntityTrackerNonliving;
import modloader.ModLoader;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Objects;

@Mixin(NetClientHandler.class)
public abstract class class_469Mixin {

    @Shadow
    private WorldClient worldClient;

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void modLoaderClientConnect(Packet1Login par1, CallbackInfo ci) {
        ModLoader.clientConnect((NetClientHandler) (Object) this, par1);
    }

    private static final List<Integer> PACKET_TYPES = Lists.newArrayList(
            10, 90,
            60, 61, 71, 65, 72, 76, 63,
            64, 66, 62, 73, 75, 1, 50, 51,
            2, 70
    );

    @Unique
    private Packet23VehicleSpawn cachedPacket = null;

    @Inject(method = "handleVehicleSpawn", at = @At("HEAD"))
    private void onEntitySpawn$getPacket(Packet23VehicleSpawn par1, CallbackInfo ci) {
        this.cachedPacket = par1;
    }

    @ModifyVariable(method = "handleVehicleSpawn",
            at = @At(value = "STORE", ordinal = 0)
    )
    private Entity modLoaderSpawnEntity(Entity value) {
        if (this.cachedPacket != null && !PACKET_TYPES.contains(this.cachedPacket.type)) {
            double var2 = (double) this.cachedPacket.xPosition / 32.0;
            double var4 = (double) this.cachedPacket.yPosition / 32.0;
            double var6 = (double) this.cachedPacket.zPosition / 32.0;

            for (EntityTrackerNonliving tracker : (Iterable<EntityTrackerNonliving>) ModLoader.getTrackers().values()) {
                if (this.cachedPacket.type == tracker.id) {
                    value = tracker.mod.spawnEntity(this.cachedPacket.type, this.worldClient, var2, var4, var6);
                    this.cachedPacket = null;
                    break;
                }
            }
        }

        return value;
    }

    @Inject(method = "handleKickDisconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;loadWorld(Lnet/minecraft/src/WorldClient;)V"))
    private void modLoaderClientDisconnect(Packet255KickDisconnect par1, CallbackInfo ci) {
        ModLoader.clientDisconnect();
    }

    @Inject(method = "handleErrorMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;loadWorld(Lnet/minecraft/src/WorldClient;)V"))
    private void modLoaderClientDisconnect(String objects, Object[] par2, CallbackInfo ci) {
        ModLoader.clientDisconnect();
    }

    @Inject(method = "handleChat", at = @At("RETURN"))
    private void modLoaderClientChat(Packet3Chat par1, CallbackInfo ci) {
        ModLoader.clientChat(par1.message);
    }

    @Inject(method = "disconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/INetworkManager;wakeThreads()V"))
    private void modLoaderClientDisconnect(CallbackInfo ci) {
        ModLoader.clientDisconnect();
    }

    @Inject(method = "handleOpenWindow", at = @At("HEAD"), cancellable = true)
    private void modLoaderClientOpenWindow(Packet100OpenWindow par1, CallbackInfo ci) {
        if (par1.inventoryType > 10 || par1.inventoryType < 0) {
            ModLoader.clientOpenWindow(par1);
            ci.cancel();
        }
    }

    @Inject(method = "handleTileEntityData",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/src/WorldClient;getBlockTileEntity(III)Lnet/minecraft/src/TileEntity;"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void modLoader$onBlockEntityUpdate(Packet132TileEntityData par1, CallbackInfo ci, TileEntity var2) {
        if (var2 != null && par1.actionType == 255 && par1.customParam1 != null) {
            String pname = par1.customParam1.getString("id");
            String tename = (String) BlockEntityAccessor.getStringClassMap().get(var2.getClass());
            if (pname != null && pname.equals(tename)) {
                var2.readFromNBT(par1.customParam1);
                ci.cancel();
            }
        }
    }

    @Inject(method = "handleCustomPayload", at = @At("RETURN"))
    private void modLoaderClientCustomPayload(Packet250CustomPayload par1, CallbackInfo ci) {
        if (!Objects.equals(par1.channel, "MC|TPack") && !Objects.equals(par1.channel, "MC|TrList")) {
            ModLoader.clientCustomPayload(par1);
        }
    }
}
