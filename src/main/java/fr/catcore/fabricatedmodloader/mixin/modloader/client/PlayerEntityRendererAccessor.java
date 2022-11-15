package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPlayer.class)
public interface PlayerEntityRendererAccessor {

    @Accessor("armorFilenamePrefix")
    static void setArmor(String[] armor) {

    }

    @Accessor("armorFilenamePrefix")
    static String[] getArmor() {
        return new String[0];
    }
}
