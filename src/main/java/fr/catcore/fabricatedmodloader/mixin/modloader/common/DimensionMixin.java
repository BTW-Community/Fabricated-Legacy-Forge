package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import fr.catcore.fabricatedmodloader.mixininterface.ILevelGeneratorType;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldProvider.class)
public class DimensionMixin {


    @Shadow public WorldChunkManager worldChunkMgr;

    @Shadow public WorldType terrainType;

    @Shadow public World worldObj;

    @Shadow public String field_82913_c;

    @Shadow public boolean hasNoSky;


    @Redirect(method = "registerWorldChunkManager", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;getTerrainType()Lnet/minecraft/src/WorldType;"))
    private WorldType injectRegisterWorldChunkManager(WorldInfo worldInfo) {
        return this.terrainType;
    }

    /**
     * @reason ModLoader patch
     * @author Risugami
     */
    /*@Overwrite
    public void registerWorldChunkManager() {
        this.worldChunkMgr = ((ILevelGeneratorType) this.terrainType).getChunkManager(this.worldObj);
    }*/

    /**
     * @reason ModLoader patch
     * @author Risugami
     */
    /*@Overwrite
    public IChunkProvider createChunkGenerator() {
        return ((ILevelGeneratorType) this.terrainType).getChunkGenerator(this.worldObj, this.field_82913_c);
    }*/

    /**
     * @reason ModLoader patch
     * @author Risugami
     */
    /*@Overwrite
    public int getAverageGroundLevel() {
        return ((ILevelGeneratorType) this.terrainType).getSeaLevel(this.worldObj);
    }*/

    /**
     * @reason ModLoader patch
     * @author Risugami
     */
    /*@Environment(EnvType.CLIENT)
    @Overwrite
    public boolean getWorldHasVoidParticles() {
        return ((ILevelGeneratorType) this.terrainType).hasVoidParticles(this.hasNoSky);
    }*/

    /**
     * @reason ModLoader patch
     * @author Risugami
     */
    /*@Environment(EnvType.CLIENT)
    @Overwrite
    public double getVoidFogYFactor() {
        return ((ILevelGeneratorType) this.terrainType).voidFadeMagnitude();
    }*/
}
