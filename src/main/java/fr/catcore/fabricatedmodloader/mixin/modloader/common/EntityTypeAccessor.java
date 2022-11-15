package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(EntityList.class)
public interface EntityTypeAccessor {

    @Accessor("stringToClassMapping")
    static Map<String, Class<?>> getClassMap() {
        return null;
    }

    @Invoker("addMapping")
    static void callRegister(Class clazz, String name, int id) {
        throw new AssertionError("@Invoker dummy body called");
    }
}
