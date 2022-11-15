package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedRandomItem.class)
public interface WeightAccessor {

    @Accessor("itemWeight")
    void setWeight(int weight);
}
