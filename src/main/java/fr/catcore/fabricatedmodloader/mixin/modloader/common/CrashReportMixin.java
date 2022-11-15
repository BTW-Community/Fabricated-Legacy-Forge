package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.*;
import modloader.ModLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {
    
    @Shadow @Final private CrashReportCategory field_85061_c;

    @Inject(method = "populateEnvironment", at = @At("RETURN"))
    private void addModLoaderSection(CallbackInfo ci) {
        this.field_85061_c.addCrashSection("ModLoader", ModLoader.getCrashReport());
    }
}
