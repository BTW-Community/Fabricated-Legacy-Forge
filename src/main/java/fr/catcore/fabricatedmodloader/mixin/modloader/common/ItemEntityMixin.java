package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import modloader.ModLoader;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getEntityItem();

    @Inject(method = "onCollideWithPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityItem;playSound(Ljava/lang/String;FF)V"))
    private void modLoaderOnItemPickup(EntityPlayer par1, CallbackInfo ci) {
        ModLoader.onItemPickup(par1, this.getEntityItem());
    }
}
