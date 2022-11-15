package fr.catcore.fabricatedmodloader.mixin.compat.toomanyitems;

import fr.catcore.fabricatedmodloader.utils.compat.TMIPrivateFieldsHelper;
import net.minecraft.TMIPrivateFields;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@Mixin(TMIPrivateFields.class)
public abstract class TMIPrivateFieldsMixin {

    @Shadow(remap = false)
    public static void unsetFinalPrivate(Field field) {
    }

    @Inject(method = "getPrivateField", at = @At("HEAD"), remap = false, cancellable = true)
    private static void injectGetPrivateField(Class class_, String obfuscated, String searge, CallbackInfoReturnable<Field> cir) {
        try {
            Map<String, String> map = TMIPrivateFieldsHelper.class2field.get(class_);
            Field field = class_.getDeclaredField(map.get(map.containsKey(obfuscated) ? obfuscated : searge));
            if (field != null){
                unsetFinalPrivate(field);
                cir.setReturnValue(field);
            }
        } catch (NoSuchFieldException e) {
            try {
                Map<String, String> map = TMIPrivateFieldsHelper.class2field.get(class_);
                String named = map.get(map.containsKey(obfuscated) ? obfuscated : searge);
                Field field = class_.getDeclaredField(map.get(named));
                if (field != null){
                    unsetFinalPrivate(field);
                    cir.setReturnValue(field);
                }
            } catch (NoSuchFieldException e2) {
                e.printStackTrace();
                throw new RuntimeException(e2);
            }
        }
    }

    @Inject(method = "getPrivateMethod", at = @At("HEAD"), remap = false, cancellable = true)
    private static void injectGetPrivateMethod(Class class_, String obfuscated, String searge, Class[] classs, CallbackInfoReturnable<Method> cir) {
        try {
            Map<String, String> map = TMIPrivateFieldsHelper.class2method.get(class_);
            Method method = class_.getDeclaredMethod(map.get(map.containsKey(obfuscated) ? obfuscated : searge), classs);
            if (method != null){
                method.setAccessible(true);
                cir.setReturnValue(method);
            }
        } catch (NoSuchMethodException e) {
            try {
                Map<String, String> map = TMIPrivateFieldsHelper.class2method.get(class_);
                String named = map.get(map.containsKey(obfuscated) ? obfuscated : searge);
                Method method = class_.getDeclaredMethod(map.get(named), classs);
                if (method != null){
                    method.setAccessible(true);
                    cir.setReturnValue(method);
                }
            } catch (NoSuchMethodException e2) {
                e.printStackTrace();
                throw new RuntimeException(e2);
            }
        }
    }
}
