package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Properties;

@Mixin(StringTranslate.class)
public interface LanguageAccessor {

    @Accessor("translateTable")
    Properties getTranslationMap();
}
