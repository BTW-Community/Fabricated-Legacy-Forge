package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.*;
import modloader.ModLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotCrafting.class)
public class CraftingResultSlotMixin {

    @Shadow private EntityPlayer thePlayer;

    @Shadow @Final private IInventory craftMatrix;

    @Inject(method = "onPickupFromSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/SlotCrafting;onCrafting(Lnet/minecraft/src/ItemStack;)V", shift = At.Shift.AFTER))
    private void modLoaderTakenFromCrafting(EntityPlayer stack, ItemStack par2, CallbackInfo ci) {
        ModLoader.takenFromCrafting(this.thePlayer, par2, this.craftMatrix);
    }
}
