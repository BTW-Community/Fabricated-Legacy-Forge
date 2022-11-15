package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import modloader.ModLoader;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SlotFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotFurnace.class)
public class FurnaceOutputSlotMixin {

    @Shadow private EntityPlayer thePlayer;

    @Inject(method = "onCrafting(Lnet/minecraft/src/ItemStack;)V", at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/src/ItemStack;onCrafting(Lnet/minecraft/src/World;Lnet/minecraft/src/EntityPlayer;I)V"))
    private void modLoaderTakenFromFurnace(ItemStack par1, CallbackInfo ci) {
        ModLoader.takenFromFurnace(this.thePlayer, par1);
    }
}
