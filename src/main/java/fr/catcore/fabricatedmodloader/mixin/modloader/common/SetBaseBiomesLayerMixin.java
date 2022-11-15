package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import fr.catcore.fabricatedmodloader.utils.SetBaseBiomesLayerData;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.GenLayer;
import net.minecraft.src.GenLayerBiome;
import net.minecraft.src.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenLayerBiome.class)
public class SetBaseBiomesLayerMixin {


    @Shadow private BiomeGenBase[] allowedBiomes;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void useModLoaderBiomePool(long l, GenLayer genLayer, WorldType worldType, CallbackInfo ci) {
        if (worldType != WorldType.DEFAULT_1_1) this.allowedBiomes = SetBaseBiomesLayerData.biomeArray;
    }
}
