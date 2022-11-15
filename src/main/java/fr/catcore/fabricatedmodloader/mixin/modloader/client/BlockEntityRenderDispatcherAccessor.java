package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TileEntityRenderer.class)
public interface BlockEntityRenderDispatcherAccessor {

    @Accessor("specialRendererMap")
    Map<Class<?>, TileEntitySpecialRenderer> getRenderers();
}
