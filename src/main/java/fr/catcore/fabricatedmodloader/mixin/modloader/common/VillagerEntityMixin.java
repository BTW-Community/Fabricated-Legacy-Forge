package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import modloader.ModLoader;
import modloader.TradeEntry;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(EntityVillager.class)
public abstract class VillagerEntityMixin extends Entity {

    @Shadow public abstract int getProfession();

    @Shadow
    protected static void addMerchantItem(MerchantRecipeList merchantRecipeList, int i, Random random, float f) {
    }

    @Shadow
    protected static void addBlacksmithItem(MerchantRecipeList merchantRecipeList, int i, Random random, float f) {
    }

    @Shadow @Final private static Map villagerStockList;

    public VillagerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "addDefaultEquipmentAndRecipies",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MerchantRecipeList;isEmpty()Z", shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void modLoaderAddTrades(int par1, CallbackInfo ci, MerchantRecipeList var2) {
        this.addModTrades(var2);
    }

    private void addModTrades(MerchantRecipeList merchantrecipelist) {
        List<TradeEntry> list = ModLoader.getTrades(this.getProfession());
        if (list != null) {
            for (TradeEntry entry : list) {
                if (entry.buying) {
                    addMerchantItem(merchantrecipelist, entry.id, this.rand, entry.chance);
                } else {
                    addBlacksmithItem(merchantrecipelist, entry.id, this.rand, entry.chance);
                }
            }

        }
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void staticInit(CallbackInfo ci) {
        List<TradeEntry> list = ModLoader.getTrades(-1);
        if (list != null) {
            for (TradeEntry entry : list) {
                if (entry.buying) {
                    if (entry.min > 0 && entry.max > 0) {
                        villagerStockList.put(entry.id, new Tuple(entry.min, entry.max));
                    }
                } else if (entry.min > 0 && entry.max > 0) {
                    villagerStockList.put(entry.id, new Tuple(entry.min, entry.max));
                }
            }
        }
    }
}
