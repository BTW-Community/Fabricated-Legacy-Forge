package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import modloader.EntityTrackerNonliving;
import modloader.ModLoader;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(EntityTrackerEntry.class)
public class TrackedEntityInstanceMixin {

    @Shadow public Entity myEntity;

    @Inject(method = "getPacketForThisEntity", at = @At(value = "NEW", target = "java/lang/IllegalArgumentException"), cancellable = true)
    private void modLoaderGetSpawnPacket(CallbackInfoReturnable<Packet> cir) {
        Iterator<EntityTrackerNonliving> i$ = ModLoader.getTrackers().values().iterator();

        EntityTrackerNonliving tracker;
        do {
            if (!i$.hasNext()) {
                throw new IllegalArgumentException("Don't know how to add " + this.myEntity.getClass() + "!");
            }

            tracker = i$.next();
        } while (!tracker.entityClass.isAssignableFrom(this.myEntity.getClass()));

        cir.setReturnValue(tracker.mod.getSpawnPacket(this.myEntity, tracker.id));
    }
}
