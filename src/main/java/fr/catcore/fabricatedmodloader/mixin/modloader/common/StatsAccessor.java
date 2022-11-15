package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.StatList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StatList.class)
public interface StatsAccessor {

    @Accessor("oneShotStats")
    static Map getIdToStat() {
        return null;
    }
}
