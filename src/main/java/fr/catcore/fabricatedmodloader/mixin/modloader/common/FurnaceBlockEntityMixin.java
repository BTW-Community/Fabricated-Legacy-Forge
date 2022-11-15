package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import modloader.ModLoader;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityFurnace.class)
public abstract class FurnaceBlockEntityMixin extends TileEntity implements IInventory {

    @Shadow protected ItemStack[] furnaceItemStacks;

    @Shadow protected abstract boolean canSmelt();

    @Unique
    private Item cachedRecipeReminder = null;

    @Inject(
            method = "smeltItem",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/src/ItemStack;stackSize:I", shift = At.Shift.AFTER)
    )
    private void modLoader$fixStack(CallbackInfo ci) {
        if (this.furnaceItemStacks[0].stackSize <= 0) this.cachedRecipeReminder = this.furnaceItemStacks[0].getItem().getContainerItem();
    }

    @Inject(method = "smeltItem", at = @At("RETURN"))
    private void modLoader$fixStackPart2(CallbackInfo ci) {
        if (this.canSmelt() && this.cachedRecipeReminder != null && this.furnaceItemStacks[0] == null) {
            this.furnaceItemStacks[0] = new ItemStack(this.cachedRecipeReminder);
            this.cachedRecipeReminder = null;
        }
    }

    @Inject(method = "getItemBurnTime", at = @At("RETURN"), cancellable = true)
    private void modLoaderBurnTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int result = cir.getReturnValue();

        if (result == 0 && stack != null) {
            cir.setReturnValue(ModLoader.addAllFuel(stack.itemID, stack.getItemDamage()));
        }
    }
}
