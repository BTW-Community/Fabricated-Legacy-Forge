package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import fr.catcore.fabricatedmodloader.mixininterface.ILevelGeneratorType;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldType.class)
public class LevelGeneratorTypeMixin implements ILevelGeneratorType {
    @Shadow
    @Final
    public static WorldType FLAT;

    @Override
    public WorldChunkManager getChunkManager(World world) {
        return (Object) this == FLAT ? new WorldChunkManagerHell(BiomeGenBase.biomeList[FlatGeneratorInfo.createFlatGeneratorFromString(world.getWorldInfo().getGeneratorOptions()).getBiome()], 0.5F, 0.5F) : new WorldChunkManager(world);
    }

    @Override
    public IChunkProvider getChunkGenerator(World world, String params) {
        return (Object) this == FLAT ? new ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), params) : new ChunkProviderGenerate(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled());
    }

    @Override
    public int getSeaLevel(World world) {
        return (Object) this != FLAT ? 64 : 4;
    }

    @Override
    public boolean hasVoidParticles(boolean flag) {
        return (Object) this != FLAT && !flag;
    }

    @Override
    public double voidFadeMagnitude() {
        return (Object) this != FLAT ? 0.03125 : 1.0;
    }
}
