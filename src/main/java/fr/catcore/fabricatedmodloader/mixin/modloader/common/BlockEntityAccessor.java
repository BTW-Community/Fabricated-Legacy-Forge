package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(TileEntity.class)
public interface BlockEntityAccessor {

    @Invoker("addMapping")
    static void callRegister(Class clazz, String stringId) {
        throw new AssertionError("@Invoker dummy body called");
    }

    @Accessor("nameToClassMap")
    static Map getStringClassMap() {
        return null;
    }
}
