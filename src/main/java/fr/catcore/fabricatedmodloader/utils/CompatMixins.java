package fr.catcore.fabricatedmodloader.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;

public class CompatMixins {
    public static final Map<String, Boolean> MIXINS = new HashMap<>();

    static {
        boolean toomanyitems = FabricLoader.getInstance().isModLoaded("toomanyitems");
        boolean reiminimap = FabricLoader.getInstance().isModLoaded("reiminimap");
        System.out.println(toomanyitems);
        MIXINS.put("fr.catcore.fabricatedmodloader.mixin.compat.toomanyitems.GuiContainerTMICompatMixin", toomanyitems);
        MIXINS.put("fr.catcore.fabricatedmodloader.mixin.compat.toomanyitems.TMIPrivateFieldsMixin", toomanyitems);
        MIXINS.put("fr.catcore.fabricatedmodloader.mixin.compat.reiminimap.ReiMinimapAdaptation", reiminimap);
//        MIXINS.put("fr.catcore.fabricatedmodloader.mixin.compat.invtweaks.FixInvTweaksLocalization", invTweaks);
//        MIXINS.put("fr.catcore.fabricatedmodloader.mixin.compat.invtweaks.FixInvTweaksReflection", invTweaks);
    }
}
