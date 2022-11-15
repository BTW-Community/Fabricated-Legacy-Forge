package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderEngine.class)
public interface class_534Accessor {

    @Accessor("texturePack")
    TexturePackList getTexturePackManager();
}
