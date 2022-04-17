package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import modloader.EntityTrackerNonliving;
import modloader.ModLoader;
import net.minecraft.client.class_469;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.thrown.*;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.class_690;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessage_S2CPacket;
import net.minecraft.network.packet.s2c.play.Disconnect_S2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawn_S2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreen_S2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Objects;

@Mixin(class_469.class)
public abstract class class_469Mixin {

    @Shadow
    private ClientWorld world;

    @Shadow
    protected abstract Entity getEntityById(int id);

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void modLoaderClientConnect(class_690 par1, CallbackInfo ci) {
        ModLoader.clientConnect((class_469) (Object) this, par1);
    }

    /**
     * @author
     */
    @Overwrite
    public void onEntitySpawn(EntitySpawn_S2CPacket packet) {
        double var2 = (double) packet.x / 32.0;
        double var4 = (double) packet.y / 32.0;
        double var6 = (double) packet.z / 32.0;
        Object var8 = null;
        if (packet.type == 10) {
            var8 = new AbstractMinecartEntity(this.world, var2, var4, var6, 0);
        } else if (packet.type == 11) {
            var8 = new AbstractMinecartEntity(this.world, var2, var4, var6, 1);
        } else if (packet.type == 12) {
            var8 = new AbstractMinecartEntity(this.world, var2, var4, var6, 2);
        } else if (packet.type == 90) {
            Entity var9 = this.getEntityById(packet.ownerId);
            if (var9 instanceof PlayerEntity) {
                var8 = new FishingBobberEntity(this.world, var2, var4, var6, (PlayerEntity) var9);
            }

            packet.ownerId = 0;
        } else if (packet.type == 60) {
            var8 = new AbstractArrowEntity(this.world, var2, var4, var6);
        } else if (packet.type == 61) {
            var8 = new SnowballEntity(this.world, var2, var4, var6);
        } else if (packet.type == 65) {
            var8 = new EnderPearlEntity(this.world, var2, var4, var6);
        } else if (packet.type == 72) {
            var8 = new EyeOfEnderEntity(this.world, var2, var4, var6);
        } else if (packet.type == 63) {
            var8 = new ExplosiveProjectileEntity(this.world, var2, var4, var6, (double) packet.velocityX / 8000.0, (double) packet.velocityY / 8000.0, (double) packet.velocityZ / 8000.0);
            packet.ownerId = 0;
        } else if (packet.type == 64) {
            var8 = new SmallFireballEntity(this.world, var2, var4, var6, (double) packet.velocityX / 8000.0, (double) packet.velocityY / 8000.0, (double) packet.velocityZ / 8000.0);
            packet.ownerId = 0;
        } else if (packet.type == 62) {
            var8 = new EggEntity(this.world, var2, var4, var6);
        } else if (packet.type == 73) {
            var8 = new PotionEntity(this.world, var2, var4, var6, packet.ownerId);
            packet.ownerId = 0;
        } else if (packet.type == 75) {
            var8 = new ExperienceBottleEntity(this.world, var2, var4, var6);
            packet.ownerId = 0;
        } else if (packet.type == 1) {
            var8 = new BoatEntity(this.world, var2, var4, var6);
        } else if (packet.type == 50) {
            var8 = new TntEntity(this.world, var2, var4, var6);
        } else if (packet.type == 51) {
            var8 = new EndCrystalEntity(this.world, var2, var4, var6);
        } else if (packet.type == 70) {
            var8 = new FallingBlockEntity(this.world, var2, var4, var6, packet.ownerId & '\uffff', packet.ownerId >> 16);
            packet.ownerId = 0;
        } else {
            Iterator i$ = ModLoader.getTrackers().values().iterator();

            while (i$.hasNext()) {
                EntityTrackerNonliving tracker = (EntityTrackerNonliving) i$.next();
                if (packet.type == tracker.id) {
                    var8 = tracker.mod.spawnEntity(packet.type, this.world, var2, var4, var6);
                    break;
                }
            }
        }

        if (var8 != null) {
            ((Entity) var8).trackedX = packet.x;
            ((Entity) var8).trackedY = packet.y;
            ((Entity) var8).trackedZ = packet.z;
            ((Entity) var8).yaw = 0.0F;
            ((Entity) var8).pitch = 0.0F;
            Entity[] var15 = ((Entity) var8).getParts();
            if (var15 != null) {
                int var10 = packet.id - ((Entity) var8).id;
                Entity[] var11 = var15;
                int var12 = var15.length;

                for (int var13 = 0; var13 < var12; ++var13) {
                    Entity var14 = var11[var13];
                    var14.id += var10;
                }
            }

            ((Entity) var8).id = packet.id;
            this.world.method_1253(packet.id, (Entity) var8);
            if (packet.ownerId > 0) {
                if (packet.type == 60) {
                    Entity var16 = this.getEntityById(packet.ownerId);
                    if (var16 instanceof MobEntity) {
                        AbstractArrowEntity var17 = (AbstractArrowEntity) var8;
                        var17.owner = var16;
                    }
                }

                ((Entity) var8).setVelocityClient((double) packet.velocityX / 8000.0, (double) packet.velocityY / 8000.0, (double) packet.velocityZ / 8000.0);
            }
        }

    }

    @Inject(method = "onDisconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;connect(Lnet/minecraft/client/world/ClientWorld;)V"))
    private void modLoaderClientDisconnect(Disconnect_S2CPacket par1, CallbackInfo ci) {
        ModLoader.clientDisconnect();
    }

    @Inject(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;connect(Lnet/minecraft/client/world/ClientWorld;)V"))
    private void modLoaderClientDisconnect(String objects, Object[] par2, CallbackInfo ci) {
        ModLoader.clientDisconnect();
    }

    @Inject(method = "onChatMessage", at = @At("RETURN"))
    private void modLoaderClientChat(ChatMessage_S2CPacket par1, CallbackInfo ci) {
        ModLoader.clientChat(par1.message);
    }

    @Inject(method = "method_1204", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;wakeThreads()V"))
    private void modLoaderClientDisconnect(CallbackInfo ci) {
        ModLoader.clientDisconnect();
    }

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    private void modLoaderClientOpenWindow(OpenScreen_S2CPacket par1, CallbackInfo ci) {
        if (par1.type > 6 || par1.type < 0) {
            ModLoader.clientOpenWindow(par1);
            ci.cancel();
        }
    }

    @Inject(method = "onCustomPayload", at = @At("RETURN"))
    private void modLoaderClientCustomPayload(CustomPayloadC2SPacket par1, CallbackInfo ci) {
        if (!Objects.equals(par1.channel, "MC|TPack") && !Objects.equals(par1.channel, "MC|TrList")) {
            ModLoader.clientCustomPayload(par1);
        }
    }
}
