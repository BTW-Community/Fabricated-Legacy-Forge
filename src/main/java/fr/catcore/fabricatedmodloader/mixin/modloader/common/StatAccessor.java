package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.StatBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatBase.class)
public interface StatAccessor {

    @Accessor("statName")
    void setStringId(String stringId);
}
