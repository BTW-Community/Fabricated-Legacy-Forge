package fr.catcore.fabricatedmodloader.mixininterface;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraft.src.WorldChunkManager;

@Deprecated
public interface ILevelGeneratorType {

    WorldChunkManager getChunkManager(World world);

    IChunkProvider getChunkGenerator(World world, String params);

    int getSeaLevel(World world);

    boolean hasVoidParticles(boolean flag);

    double voidFadeMagnitude();
}
