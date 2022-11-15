package fr.catcore.fabricatedmodloader.utils.compat;

import net.minecraft.src.*;

import java.util.HashMap;
import java.util.Map;

public class TMIPrivateFieldsHelper {
    public static final Map<Class, Map<String, String>> class2field = new HashMap<>();
    public static final Map<Class, Map<String, String>> class2method = new HashMap<>();

    static {
        Map<String, String> map = new HashMap<>();
        map.put("b", "xPos");
        map.put("xPos", "field_1117");
        map.put("c", "yPos");
        map.put("yPos", "field_1118");
        map.put("d", "width");
        map.put("width", "field_1119");
        map.put("e", "height");
        map.put("height", "field_1120");
        class2field.put(GuiTextField.class, map);
        map = new HashMap<>();
        map.put("s", "selectedTabIndex");
        map.put("selectedTabIndex", "field_1376");
        map.put("w", "searchField");
        map.put("searchField", "field_1380");
        class2field.put(GuiContainerCreative.class, map);
        map = new HashMap<>();
        map.put("k", "currentGameType");
        map.put("currentGameType", "field_1656");
        class2field.put(PlayerControllerMP.class, map);
        map = new HashMap<>();
        map.put("b", "theSlot");
        map.put("theSlot", "field_1386");
        class2field.put(SlotCreativeInventory.class, map);
        map = new HashMap<>();
        map.put("e", "itemDamage");
        map.put("itemDamage", "field_4380");
        class2field.put(ItemStack.class, map);
        map = new HashMap<>();
        map.put("aw", "mobSpawner");
        map.put("mobSpawner", "field_405");
        map.put("aW", "snow");
        map.put("snow", "field_379");
        class2field.put(Block.class, map);
        map = new HashMap<>();
        map.put("d", "IDtoClassMapping");
        map.put("IDtoClassMapping", "field_3270");
        class2field.put(EntityList.class, map);

        map = new HashMap<>();
        map.put("b", "setCurrentCreativeTab");
        map.put("setCurrentCreativeTab", "method_1146");
        class2method.put(GuiContainerCreative.class, map);
        map = new HashMap<>();
        map.put("a", "setBlockBounds");
        map.put("setBlockBounds", "method_394");
        class2method.put(Block.class, map);
        map = new HashMap<>();
        map.put("func_74735_a", "load");
        map.put("load", "method_1649");
        map.put("func_74734_a", "write");
        map.put("write", "method_1650");
        class2method.put(NBTTagList.class, map);
        map = new HashMap<>();
        map.put("a", "drawGradientRect");
        map.put("drawGradientRect", "method_989");
        class2method.put(Gui.class, map);
    }
}
