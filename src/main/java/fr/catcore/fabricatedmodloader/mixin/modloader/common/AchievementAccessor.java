package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Achievement.class)
public interface AchievementAccessor {

    @Accessor
    void setAchievementDescription(String description);
}
