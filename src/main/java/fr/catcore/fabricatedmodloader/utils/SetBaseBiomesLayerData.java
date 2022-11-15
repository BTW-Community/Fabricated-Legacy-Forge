package fr.catcore.fabricatedmodloader.utils;

import net.minecraft.src.*;

public class SetBaseBiomesLayerData {
    public static BiomeGenBase[] biomeArray;

    static {
        biomeArray = new BiomeGenBase[]{BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.jungle};
    }
}
