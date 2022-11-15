package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import modloader.EntityTrackerNonliving;
import modloader.ModLoader;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTracker.class)
public abstract class EntityTrackerMixin {

    @Shadow public abstract void addEntityToTracker(Entity par1Entity, int par2, int par3, boolean par4);

    @Inject(method = "addEntityToTracker(Lnet/minecraft/src/Entity;)V", at = @At("RETURN"))
    private void modLoaderAddTrackers(Entity entity, CallbackInfo ci) {
        if (!(
                entity instanceof EntityPlayerMP
                        || entity instanceof EntityFishHook
                        || entity instanceof EntityArrow
                        || entity instanceof EntitySmallFireball
                        || entity instanceof EntityLargeFireball
                        || entity instanceof EntitySnowball
                        || entity instanceof EntityEnderPearl
                        || entity instanceof EntityEnderEye
                        || entity instanceof EntityEgg
                        || entity instanceof EntityPotion
                        || entity instanceof EntityExpBottle
                        || entity instanceof EntityFireworkRocket
                        || entity instanceof EntityItem
                        || entity instanceof EntityMinecart
                        || entity instanceof EntityBoat
                        || entity instanceof EntitySquid
                        || entity instanceof EntityWither
                        || entity instanceof EntityBat
                        || entity instanceof IAnimals
                        || entity instanceof EntityDragon
                        || entity instanceof EntityTNTPrimed
                        || entity instanceof EntityFallingSand
                        || entity instanceof EntityPainting
                        || entity instanceof EntityXPOrb
                        || entity instanceof EntityEnderCrystal
                        || entity instanceof EntityItemFrame
        )) {
            for (EntityTrackerNonliving tracker : (Iterable<EntityTrackerNonliving>) ModLoader.getTrackers().values()) {
                if (tracker.entityClass.isAssignableFrom(entity.getClass())) {
                    this.addEntityToTracker(entity, tracker.viewDistance, tracker.updateFrequency, tracker.trackMotion);
                }
            }
        }
    }


}
