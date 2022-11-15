package modloader;

import fr.catcore.fabricatedmodloader.mixin.modloader.client.BlockEntityRenderDispatcherAccessor;
import fr.catcore.fabricatedmodloader.mixin.modloader.client.PlayerEntityRendererAccessor;
import fr.catcore.fabricatedmodloader.mixin.modloader.client.SoundSystemAccessor;
import fr.catcore.fabricatedmodloader.mixin.modloader.client.class_534Accessor;
import fr.catcore.fabricatedmodloader.mixin.modloader.common.*;
import fr.catcore.fabricatedmodloader.mixininterface.ISoundLoader;
import fr.catcore.fabricatedmodloader.remapping.Log;
import fr.catcore.fabricatedmodloader.utils.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@SuppressWarnings("unused")
public final class ModLoader {
    private static final Map<Integer, BaseMod> blockModels = new HashMap<>();
    private static final Map<Integer, Boolean> blockSpecialInv = new HashMap<>();
    private static final File cfgdir = new File(FabricLoader.getInstance().getGameDir().toFile(), "/config/");
    private static final File cfgfile;
    public static Level cfgLoggingLevel;
    private static long clock = 0L;
    private static Field field_modifiers = null;
    private static boolean hasInit = false;
    private static final Map<BaseMod, Boolean> inGameHooks = new HashMap<>();
    private static final Map<BaseMod, Boolean> inGUIHooks = new HashMap<>();
    private static Minecraft instance = null;
    private static final Map<BaseMod, Map<KeyBinding, Boolean[]>> keyList = new HashMap<>();
    private static String langPack = null;
    private static final Map<String, Map<String, String>> localizedStrings = new HashMap<>();
    private static final File logfile = new File(FabricLoader.getInstance().getGameDir().toFile(), "ModLoader.txt");
    private static final Logger logger = Logger.getLogger("ModLoader");
    private static FileHandler logHandler = null;
    private static final LinkedList<BaseMod> modList = new LinkedList<>();
    private static int nextBlockModelID = 1000;
    private static final Map<Integer, Map<String, Integer>> overrides = new HashMap<>();
    private static final Map<String, BaseMod> packetChannels = new HashMap<>();
    public static final Properties props = new Properties();
    private static BiomeGenBase[] standardBiomes;
    public static final String VERSION = "ModLoader 1.5.2";
    private static NetClientHandler clientHandler = null;
    private static final List<ICommand> commandList = new LinkedList<>();
    private static final Map<Integer, List<TradeEntry>> tradeItems = new HashMap<>();
    private static final Map<Integer, BaseMod> containerGUIs = new HashMap<>();
    private static final Map<Class<?>, EntityTrackerNonliving> trackers = new HashMap<>();
    private static final Map<Item, IBehaviorDispenseItem> dispenserBehaviors = new HashMap<>();
    private static final Map<String, TextureStitched> customTextures = new HashMap<>();
    private static SoundPool soundPoolSounds;
    private static SoundPool soundPoolStreaming;
    private static SoundPool soundPoolMusic;

    public static void addAchievementDesc(Achievement achievement, String s, String s1) {
        try {
            if (achievement.getName().contains(".")) {
                String[] as = achievement.getName().split("\\.");
                if (as.length == 2) {
                    String s2 = as[1];
                    addLocalization("achievement." + s2, s);
                    addLocalization("achievement." + s2 + ".desc", s1);
                    ((StatAccessor) achievement).setStringId(StatCollector.translateToLocal("achievement." + s2));
                    ((AchievementAccessor) achievement).setAchievementDescription(StatCollector.translateToLocal("achievement." + s2 + ".desc"));
                } else {
                    ((StatAccessor) achievement).setStringId(s);
                    ((AchievementAccessor) achievement).setAchievementDescription(s1);
                }
            } else {
                ((StatAccessor) achievement).setStringId(s);
                ((AchievementAccessor) achievement).setAchievementDescription(s1);
            }
        } catch (IllegalArgumentException | SecurityException var5) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader AddAchievementDesc", var5);
            throwException(var5);
        }

    }

    public static void addEntityTracker(BaseMod mod, Class entityClass, int id, int viewDistance, int updateFrequency, boolean trackMotion) {
        if (entityClass == null) {
            throw new IllegalArgumentException();
        } else {
            if (!Entity.class.isAssignableFrom(entityClass)) {
                Exception exception = new Exception(entityClass.getCanonicalName() + " is not an entity.");
                Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader addEntityTracker", exception);
                throwException(exception);
            }

            trackers.put(entityClass, new EntityTrackerNonliving(mod, entityClass, id, viewDistance, updateFrequency, trackMotion));
        }
    }

    public static Map getTrackers() {
        return Collections.unmodifiableMap(trackers);
    }

    public static int addAllFuel(int id, int metadata) {
        Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Finding fuel for " + id);
        int result = 0;

        for (Iterator<BaseMod> iter = modList.iterator(); iter.hasNext() && result == 0; result = iter.next().addFuel(id, metadata)) {
        }

        if (result != 0) {
            Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Returned " + result);
        }

        return result;
    }

    private static boolean addAllRenderersCalled = false;
    public static void addAllRenderers(Map renderers) {
        if (!hasInit) {
            //if (true) return;
            init();
            Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Initialized");
        }
        if (addAllRenderersCalled) return;
        addAllRenderersCalled = true;
        for (BaseMod mod : modList) {
            mod.addRenderer(renderers);
        }

    }

    public static int addArmor(String s) {
        try {
            String[] as = PlayerEntityRendererAccessor.getArmor();
            List<String> list = Arrays.asList(as);
            ArrayList<String> arraylist = new ArrayList<>(list);
            if (!arraylist.contains(s)) {
                arraylist.add(s);
            }

            int i = arraylist.indexOf(s);
            PlayerEntityRendererAccessor.setArmor(arraylist.toArray(new String[0]));
            return i;
        } catch (IllegalArgumentException var5) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader AddArmor", var5);
            throwException("An impossible error has occured!", var5);
        }

        return -1;
    }

    public static void addBiome(BiomeGenBase biomegenbase) {
        BiomeGenBase[] abiomegenbase = SetBaseBiomesLayerData.biomeArray;
        List<BiomeGenBase> list = Arrays.asList(abiomegenbase);
        ArrayList<BiomeGenBase> arraylist = new ArrayList<>(list);
        if (!arraylist.contains(biomegenbase)) {
            arraylist.add(biomegenbase);
        }

        SetBaseBiomesLayerData.biomeArray = arraylist.toArray(new BiomeGenBase[0]);
    }

    public static void addCommand(ICommand cmd) {
        commandList.add(cmd);
    }

    public static void addDispenserBehavior(Item item, IBehaviorDispenseItem behavior) {
        dispenserBehaviors.put(item, behavior);
    }

    public static void registerServer(MinecraftServer server) {
        ICommandManager manager = server.getCommandManager();
        if (manager instanceof CommandHandler) {
            CommandHandler handler = (CommandHandler) manager;

            for (ICommand cmd : commandList) {
                handler.registerCommand(cmd);
            }

            for (Map.Entry<Item, IBehaviorDispenseItem> entry : dispenserBehaviors.entrySet()) {
                BlockDispenser.dispenseBehaviorRegistry.putObject(entry.getKey(), entry.getValue());
            }
        }
    }

    public static void addLocalization(String s, String s1) {
        addLocalization(s, "en_US", s1);
    }

    public static void addLocalization(String s, String s1, String s2) {
        Map<String, String> obj;
        if (localizedStrings.containsKey(s1)) {
            obj = localizedStrings.get(s1);
        } else {
            obj = new HashMap<>();
            localizedStrings.put(s1, obj);
        }

        obj.put(s, s2);
    }

    public static void addTrade(int profession, TradeEntry entry) {
        List<TradeEntry> list;
        if (tradeItems.containsKey(profession)) {
            list = tradeItems.get(profession);
        } else {
            list = new LinkedList<>();
            tradeItems.put(profession, list);
        }

        list.add(entry);
    }

    public static List getTrades(int profession) {
        if (profession != -1) {
            return tradeItems.containsKey(profession) ? Collections.unmodifiableList(tradeItems.get(profession)) : null;
        } else {
            List<TradeEntry> list = new LinkedList<>();

            for (List<TradeEntry> entry : tradeItems.values()) {
                list.addAll(entry);
            }

            return list;
        }
    }

    private static void addMod(ClassLoader classloader, String s) {
        try {
            String s1 = s.split("\\.")[0];
            if (s1.contains("$")) {
                return;
            }

            if (props.containsKey(s1) && (props.getProperty(s1).equalsIgnoreCase("no") || props.getProperty(s1).equalsIgnoreCase("off"))) {
                return;
            }

            Class<?> class1 = classloader.loadClass(s1.replace("/", "."));
            if (!BaseMod.class.isAssignableFrom(class1)) {
                return;
            }

            setupProperties(class1);
            BaseMod basemod = (BaseMod) class1.getDeclaredConstructor().newInstance();
            modList.add(basemod);
            Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Mod Initialized: \"" + basemod + "\" from " + s);
            Log.info(Constants.MODLOADER_LOG_CATEGORY, "Mod Initialized: " + basemod);
        } catch (Throwable var6) {
            Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Failed to load mod from \"" + s + "\"");
            Log.info(Constants.MODLOADER_LOG_CATEGORY, "Failed to load mod from \"" + s + "\"");
            var6.printStackTrace();
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader addMod", var6);
            throwException(var6);
        }

    }

    public static void addName(Object obj, String s) {
        addName(obj, "en_US", s);
    }

    public static void addName(Object obj, String s, String s1) {
        String s2 = null;
        Exception exception1;
        if (obj instanceof Item) {
            Item item = (Item) obj;
            if (item.getUnlocalizedName() != null) {
                s2 = item.getUnlocalizedName() + ".name";
            }
        } else if (obj instanceof Block) {
            Block block = (Block) obj;
            if (block.getUnlocalizedName() != null) {
                s2 = block.getUnlocalizedName() + ".name";
            }
        } else if (obj instanceof ItemStack) {
            ItemStack itemstack = (ItemStack) obj;
            String s3 = Item.itemsList[itemstack.itemID].getUnlocalizedName(itemstack);
            if (s3 != null) {
                s2 = s3 + ".name";
            }
        } else {
            exception1 = new Exception(obj.getClass().getName() + " cannot have name attached to it!");
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader AddName", exception1);
            throwException(exception1);
        }

        if (s2 != null) {
            addLocalization(s2, s, s1);
        } else {
            exception1 = new Exception(obj + " is missing name tag!");
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader AddName", exception1);
            throwException(exception1);
        }

    }

    public static void addRecipe(ItemStack itemstack, Object... aobj) {
        ((RecipeDispatcherAccessor) CraftingManager.getInstance()).registerShapedRecipe_invoker(itemstack, aobj);
    }

    public static void addShapelessRecipe(ItemStack itemstack, Object... aobj) {
        ((RecipeDispatcherAccessor) CraftingManager.getInstance()).registerShapelessRecipe_invoker(itemstack, aobj);
    }

    public static void addSmelting(int i, ItemStack itemstack, float xp) {
        FurnaceRecipes.smelting().addSmelting(i, itemstack, xp);
    }

    public static void addSpawn(Class class1, int i, int j, int k, EnumCreatureType enumcreaturetype) {
        addSpawn(class1, i, j, k, enumcreaturetype, null);
    }

    public static void addSpawn(Class class1, int i, int j, int k, EnumCreatureType enumcreaturetype, BiomeGenBase[] abiomegenbase) {
        if (class1 == null) {
            throw new IllegalArgumentException("entityClass cannot be null");
        } else if (enumcreaturetype == null) {
            throw new IllegalArgumentException("spawnList cannot be null");
        } else {
            if (abiomegenbase == null) {
                abiomegenbase = standardBiomes;
            }

            for (BiomeGenBase BiomeGenBase : abiomegenbase) {
                List<SpawnListEntry> list = BiomeGenBase.getSpawnableList(enumcreaturetype);
                if (list != null) {
                    boolean flag = false;

                    for (SpawnListEntry spawnlistentry : list) {
                        if (spawnlistentry.entityClass == class1) {
                            ((WeightAccessor) spawnlistentry).setWeight(i);
                            spawnlistentry.minGroupCount = j;
                            spawnlistentry.maxGroupCount = k;
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        list.add(new SpawnListEntry(class1, i, j, k));
                    }
                }
            }

        }
    }

    public static void addSpawn(String s, int i, int j, int k, EnumCreatureType enumcreaturetype) {
        addSpawn(s, i, j, k, enumcreaturetype, null);
    }

    public static void addSpawn(String s, int i, int j, int k, EnumCreatureType enumcreaturetype, BiomeGenBase[] abiomegenbase) {
        Class<?> class1 = EntityTypeAccessor.getClassMap().get(s);
        if (class1 != null && EntityLiving.class.isAssignableFrom(class1)) {
            addSpawn(class1, i, j, k, enumcreaturetype, abiomegenbase);
        }

    }

    public static void genericContainerRemoval(World world, int i, int j, int k) {
        IInventory iinventory = (IInventory) world.getBlockTileEntity(i, j, k);
        if (iinventory != null) {
            for (int l = 0; l < iinventory.getSizeInventory(); ++l) {
                ItemStack itemstack = iinventory.getStackInSlot(l);
                if (itemstack != null) {
                    double d = world.rand.nextDouble() * 0.8D + 0.1D;
                    double d1 = world.rand.nextDouble() * 0.8D + 0.1D;

                    EntityItem entityitem;
                    for (double d2 = world.rand.nextDouble() * 0.8D + 0.1D; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
                        int i1 = world.rand.nextInt(21) + 10;
                        if (i1 > itemstack.stackSize) {
                            i1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= i1;
                        entityitem = new EntityItem(world, (double) i + d, (double) j + d1, (double) k + d2, new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
                        double d3 = 0.05D;
                        entityitem.motionX = world.rand.nextGaussian() * d3;
                        entityitem.motionY = world.rand.nextGaussian() * d3 + 0.2D;
                        entityitem.motionZ = world.rand.nextGaussian() * d3;
                        if (itemstack.hasTagCompound()) {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                        }
                    }

                    iinventory.setInventorySlotContents(l, null);
                }
            }
        }

    }

    public static List getLoadedMods() {
        return Collections.unmodifiableList(modList);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static Minecraft getMinecraftInstance() {
        if (instance == null) {
            instance = Minecraft.getMinecraft();
        }

        return instance;
    }

    public static Object getPrivateValue(Class class1, Object obj, int i) throws IllegalArgumentException, SecurityException {
        try {
            Field field = class1.getDeclaredFields()[i];
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException var4) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader getPrivateValue", var4);
            throwException("An impossible error has occured!", var4);
            return null;
        }
    }

    public static Object getPrivateValue(Class class1, Object obj, String s) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        try {
            Field field = class1.getDeclaredField(s);
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException var4) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader getPrivateValue", var4);
            throwException("An impossible error has occured!", var4);
            return null;
        }
    }

    public static int getUniqueBlockModelID(BaseMod basemod, boolean flag) {
        int i = nextBlockModelID++;
        blockModels.put(i, basemod);
        blockSpecialInv.put(i, flag);
        return i;
    }

    private static void init() {
        hasInit = true;

        try {
            instance = Minecraft.getMinecraft();
            instance.entityRenderer = new EntityRendererProxy(instance);
            soundPoolSounds = ((SoundSystemAccessor) instance.sndManager).getSoundsLoader();
            soundPoolStreaming = ((SoundSystemAccessor) instance.sndManager).getStreamingLoader();
            soundPoolMusic = ((SoundSystemAccessor) instance.sndManager).getMusicLoader();
            Field[] afield = BiomeGenBase.class.getDeclaredFields();
            LinkedList<BiomeGenBase> linkedlist = new LinkedList<>();

            for (Field field : afield) {
                Class<?> class1 = field.getType();
                if ((field.getModifiers() & 8) != 0 && class1.isAssignableFrom(BiomeGenBase.class)) {
                    BiomeGenBase biomegenbase = (BiomeGenBase) field.get(null);
                    if (!(biomegenbase instanceof BiomeGenHell) && !(biomegenbase instanceof BiomeGenEnd)) {
                        linkedlist.add(biomegenbase);
                    }
                }
            }

            standardBiomes = linkedlist.toArray(new BiomeGenBase[0]);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException var10) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader init", var10);
            throwException(var10);
            throw new RuntimeException(var10);
        }

        try {
            loadConfig();
            if (props.containsKey("loggingLevel")) {
                cfgLoggingLevel = Level.parse(props.getProperty("loggingLevel"));
            }

            if (props.containsKey("grassFix")) {
                class_535Data.cfgGrassFix = Boolean.parseBoolean(props.getProperty("grassFix"));
            }

            logger.setLevel(cfgLoggingLevel);
            if ((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null) {
                logHandler = new FileHandler(logfile.getPath());
                logHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(logHandler);
            }

            Log.debug(Constants.MODLOADER_LOG_CATEGORY, "ModLoader 1.5.2 Initializing...");
            Log.info(Constants.MODLOADER_LOG_CATEGORY, "ModLoader 1.5.2 Initializing...");
            addModsToClassPath();
            sortModList();

            for (BaseMod basemod : modList) {
                basemod.load();
                Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Mod Loaded: \"" + basemod + "\"");
                Log.info(Constants.MODLOADER_LOG_CATEGORY, "Mod Loaded: " + basemod);
                if (!props.containsKey(basemod.getClass().getSimpleName())) {
                    props.setProperty(basemod.getClass().getSimpleName(), "on");
                }
            }

            for (BaseMod basemod1 : modList) {
                basemod1.modsLoaded();
            }

            Log.info(Constants.MODLOADER_LOG_CATEGORY, "Done.");
            props.setProperty("loggingLevel", cfgLoggingLevel.getName());
            props.setProperty("grassFix", Boolean.toString(class_535Data.cfgGrassFix));
            instance.gameSettings.keyBindings = registerAllKeys(instance.gameSettings.keyBindings);
            instance.gameSettings.loadOptions();
            initStats();
            saveConfig();
        } catch (Throwable var9) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader init", var9);
            throwException("ModLoader has failed to initialize.", var9);
            if (logHandler != null) {
                logHandler.close();
            }

            throw new RuntimeException(var9);
        }
    }

    private static void initStats() {
        int j;
        String s2;
        for (j = 0; j < Block.blocksList.length; ++j) {
            if (!StatsAccessor.getIdToStat().containsKey(16777216 + j) && Block.blocksList[j] != null && Block.blocksList[j].getEnableStats()) {
                s2 = StatCollector.translateToLocalFormatted("stat.mineBlock", Block.blocksList[j].getLocalizedName());
                StatList.mineBlockStatArray[j] = (new StatCrafting(16777216 + j, s2, j)).registerStat();
                StatList.objectMineStats.add(StatList.mineBlockStatArray[j]);
            }
        }

        for (j = 0; j < Item.itemsList.length; ++j) {
            if (!StatsAccessor.getIdToStat().containsKey(16908288 + j) && Item.itemsList[j] != null) {
                s2 = StatCollector.translateToLocalFormatted("stat.useItem", Item.itemsList[j].getStatName());
                StatList.objectUseStats[j] = (new StatCrafting(16908288 + j, s2, j)).registerStat();
                if (j >= Block.blocksList.length) {
                    StatList.itemStats.add(StatList.objectUseStats[j]);
                }
            }

            if (!StatsAccessor.getIdToStat().containsKey(16973824 + j) && Item.itemsList[j] != null && Item.itemsList[j].isDamageable()) {
                s2 = StatCollector.translateToLocalFormatted("stat.breakItem", Item.itemsList[j].getStatName());
                StatList.objectBreakStats[j] = (new StatCrafting(16973824 + j, s2, j)).registerStat();
            }
        }

        HashSet<Integer> hashset = new HashSet<>();

        for (IRecipe obj : (Iterable<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
            ItemStack output = obj.getRecipeOutput();
            if (output != null) hashset.add(output.itemID);
        }

        for (ItemStack obj1 : (Iterable<ItemStack>) FurnaceRecipes.smelting().getSmeltingList().values()) {
            hashset.add(obj1.itemID);
        }

        for (int k : hashset) {
            if (!StatsAccessor.getIdToStat().containsKey(16842752 + k) && Item.itemsList[k] != null) {
                String s3 = StatCollector.translateToLocalFormatted("stat.craftItem", Item.itemsList[k].getStatName());
                StatList.objectCraftStats[k] = (new StatCrafting(16842752 + k, s3, k)).registerStat();
            }
        }

    }

    public static boolean isGUIOpen(Class class1) {
        Minecraft minecraft = getMinecraftInstance();
        if (class1 == null) {
            return minecraft.currentScreen == null;
        } else {
            return class1.isInstance(minecraft.currentScreen);
        }
    }

    public static boolean isModLoaded(String s) {
        Iterator<BaseMod> iterator = modList.iterator();

        BaseMod basemod;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            basemod = iterator.next();
        } while (!s.contentEquals(basemod.getName()));

        return true;
    }

    public static void loadConfig() throws IOException {
        cfgdir.mkdir();
        if (cfgfile.exists() || cfgfile.createNewFile()) {
            if (cfgfile.canRead()) {
                FileInputStream fileinputstream = new FileInputStream(cfgfile);
                props.load(fileinputstream);
                fileinputstream.close();
            }

        }
    }

    public static BufferedImage loadImage(RenderEngine renderengine, String s) throws Exception {
        TexturePackList texturepacklist = ((class_534Accessor) renderengine).getTexturePackManager();
        InputStream inputstream = texturepacklist.getSelectedTexturePack().getResourceAsStream(s);
        if (inputstream == null) {
            throw new Exception("Image not found: " + s);
        } else {
            BufferedImage bufferedimage = ImageIO.read(inputstream);
            if (bufferedimage == null) {
                throw new Exception("Image corrupted: " + s);
            } else {
                return bufferedimage;
            }
        }
    }

    public static void onItemPickup(EntityPlayer entityplayer, ItemStack itemstack) {
        for (BaseMod basemod : modList) {
            basemod.onItemPickup(entityplayer, itemstack);
        }

    }

    public static void onTick(float f, Minecraft minecraft) {
        minecraft.mcProfiler.endSection();
        minecraft.mcProfiler.endSection();
        minecraft.mcProfiler.startSection("modtick");
        if (!hasInit) {
            init();
            Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Initialized");
        }

        if (langPack == null || !Objects.equals(StringTranslate.getInstance().getCurrentLanguage(), langPack)) {
            Properties properties = ((LanguageAccessor) StringTranslate.getInstance()).getTranslationMap();

            langPack = StringTranslate.getInstance().getCurrentLanguage();
            if (properties != null) {
                if (localizedStrings.containsKey("en_US")) {
                    properties.putAll(localizedStrings.get("en_US"));
                }

                if (!langPack.contentEquals("en_US") && localizedStrings.containsKey(langPack)) {
                    properties.putAll(localizedStrings.get(langPack));
                }
            }
        }

        long l = 0L;
        if (minecraft.thePlayer != null && minecraft.thePlayer.worldObj != null) {
            l = minecraft.thePlayer.worldObj.getWorldTime();
            Iterator<Map.Entry<BaseMod, Boolean>> iterator1 = inGameHooks.entrySet().iterator();
            Map.Entry<BaseMod, Boolean> entry;

            label126:
            while (true) {
                do {
                    if (!iterator1.hasNext()) {
                        break label126;
                    }

                    entry = iterator1.next();
                } while (clock == l && entry.getValue());

                if (!entry.getKey().onTickInGame(f, minecraft)) {
                    iterator1.remove();
                }
            }
        }

        if (minecraft.thePlayer != null && minecraft.currentScreen != null) {
            Iterator<Map.Entry<BaseMod, Boolean>> iterator1 = inGUIHooks.entrySet().iterator();
            Map.Entry<BaseMod, Boolean> entry;

            label112:
            while (true) {
                do {
                    if (!iterator1.hasNext()) {
                        break label112;
                    }

                    entry = iterator1.next();
                } while (clock == l && entry.getValue() & minecraft.thePlayer.worldObj != null);

                if (!entry.getKey().onTickInGUI(f, minecraft, minecraft.currentScreen)) {
                    iterator1.remove();
                }
            }
        }

        if (clock != l) {
            Iterator<Map.Entry<BaseMod, Map<KeyBinding, Boolean[]>>> iterator1 = keyList.entrySet().iterator();
            Map.Entry<BaseMod, Map<KeyBinding, Boolean[]>> entry;

            label98:
            while (iterator1.hasNext()) {
                entry = iterator1.next();
                Iterator<Map.Entry<KeyBinding, Boolean[]>> iterator3 = entry.getValue().entrySet().iterator();

                while (true) {
                    Map.Entry<KeyBinding, Boolean[]> entry3;
                    boolean flag;
                    Boolean[] aflag;
                    boolean flag1;
                    do {
                        do {
                            if (!iterator3.hasNext()) {
                                continue label98;
                            }

                            entry3 = iterator3.next();
                            int i = entry3.getKey().keyCode;
                            if (i < 0) {
                                i += 100;
                                flag = Mouse.isButtonDown(i);
                            } else {
                                flag = Keyboard.isKeyDown(i);
                            }

                            aflag = entry3.getValue();
                            flag1 = aflag[1];
                            aflag[1] = flag;
                        } while (!flag);
                    } while (flag1 && !aflag[0]);

                    entry.getKey().keyboardEvent(entry3.getKey());
                }
            }
        }

        clock = l;
        minecraft.mcProfiler.endSection();
        minecraft.mcProfiler.startSection("render");
        minecraft.mcProfiler.startSection("gameRenderer");
    }

    public static void openGUI(EntityPlayer entityplayer, GuiScreen guiscreen) {
        if (!hasInit) {
            init();
            Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Initialized");
        }

        Minecraft minecraft = getMinecraftInstance();
        if (minecraft.thePlayer == entityplayer) {
            if (guiscreen != null) {
                minecraft.displayGuiScreen(guiscreen);
            }

        }
    }

    public static void populateChunk(IChunkProvider ichunkprovider, int i, int j, World world) {
        if (!hasInit) {
            init();
            Log.debug(Constants.MODLOADER_LOG_CATEGORY, "Initialized");
        }

        Random random = new Random(world.getSeed());
        long l = random.nextLong() / 2L * 2L + 1L;
        long l1 = random.nextLong() / 2L * 2L + 1L;
        random.setSeed((long) i * l + (long) j * l1 ^ world.getSeed());

        for (BaseMod basemod : modList) {
            if (world.provider.isSurfaceWorld()) {
                basemod.generateSurface(world, random, i << 4, j << 4);
            } else if (world.provider.isHellWorld) {
                basemod.generateNether(world, random, i << 4, j << 4);
            }
        }

    }

    private static void addModsToClassPath() throws IllegalArgumentException, SecurityException {
        ClassLoader classloader = Minecraft.class.getClassLoader();

        FakeModManager.getMods().forEach(modEntry -> modEntry.sounds.forEach((soundType, strings) -> {
            ISoundLoader SoundPool = (ISoundLoader) (soundType == MLModEntry.SoundType.sound ? soundPoolSounds : soundType == MLModEntry.SoundType.music ? soundPoolMusic : soundPoolStreaming);
            for (String soundName : strings) {
                try {
                    SoundPool.addSound(soundName, new URL(String.format("jar:%s!/%s", modEntry.file, "resources/" + soundType.name() + "/" + soundName)));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }));
        FakeModManager.getMods().forEach(modEntry -> addMod(classloader, modEntry.initClass));
    }

    public static void clientCustomPayload(Packet250CustomPayload packet) {
        if (packet.channel.equals("ML|OpenTE")) {
            try {
                DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.data));
                int guiID = stream.read();
                int contID = stream.readInt();
                int x = stream.readInt();
                int y = stream.readInt();
                int z = stream.readInt();
                int dim = (byte) stream.read();
                EntityClientPlayerMP player = instance.thePlayer;
                if (player.dimension != dim) {
                    return;
                }

                if (containerGUIs.containsKey(contID)) {
                    BaseMod basemod = containerGUIs.get(contID);
                    if (basemod != null) {
                        GuiContainer gui = basemod.getContainerGUI(player, contID, x, y, z);
                        if (gui == null) {
                            return;
                        }

                        instance.displayGuiScreen(gui);
                        player.openContainer.windowId = guiID;
                    }
                }
            } catch (IOException var11) {
                var11.printStackTrace();
            }
        } else if (packetChannels.containsKey(packet.channel)) {
            BaseMod basemod = packetChannels.get(packet.channel);
            if (basemod != null) {
                basemod.clientCustomPayload(clientHandler, packet);
            }
        }

    }

    public static void serverCustomPayload(NetServerHandler serverHandler, Packet250CustomPayload packet250custompayload) {
        if (packetChannels.containsKey(packet250custompayload.channel)) {
            BaseMod basemod = packetChannels.get(packet250custompayload.channel);
            if (basemod != null) {
                basemod.serverCustomPayload(serverHandler, packet250custompayload);
            }
        }

    }

    public static void registerContainerID(BaseMod mod, int id) {
        containerGUIs.put(id, mod);
    }

    public static void clientOpenWindow(Packet100OpenWindow par1Packet100OpenWindow) {
    }

    public static void serverOpenWindow(EntityPlayerMP player, Container container, int id, int x, int y, int z) {
        try {
            Field winIDField = EntityPlayerMP.class.getDeclaredFields()[16];
            winIDField.setAccessible(true);
            System.out.println(winIDField.getName());
            int winID = winIDField.getInt(player);
            winID = winID % 100 + 1;
            winIDField.setInt(player, winID);
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bytestream);
            stream.write(winID);
            stream.writeInt(id);
            stream.writeInt(x);
            stream.writeInt(y);
            stream.writeInt(z);
            stream.write(player.dimension);
            player.playerNetServerHandler.sendPacketToPlayer(new Packet250CustomPayload("ML|OpenTE", bytestream.toByteArray()));
            player.openContainer = container;
            player.openContainer.windowId = winID;
            player.openContainer.onCraftGuiClosed(player);
        } catch (Exception var10) {
            var10.printStackTrace();
        }

    }

    public static KeyBinding[] registerAllKeys(KeyBinding[] akeybinding) {
        LinkedList<KeyBinding> linkedlist = new LinkedList<>(Arrays.asList(akeybinding));

        for (Map<KeyBinding, Boolean[]> keyBindingMap : keyList.values()) {
            linkedlist.addAll(keyBindingMap.keySet());
        }

        return linkedlist.toArray(new KeyBinding[0]);
    }

    public static void registerBlock(Block block) {
        registerBlock(block, null);
    }

    public static void registerBlock(Block block, Class class1) {
        try {
            if (block == null) {
                throw new IllegalArgumentException("block parameter cannot be null.");
            }

            int i = block.blockID;
            ItemBlock itemblock;
            if (class1 != null) {
                itemblock = (ItemBlock) class1.getConstructor(Integer.TYPE).newInstance(i - 256);
            } else {
                itemblock = new ItemBlock(i - 256);
            }

            if (Block.blocksList[i] != null && Item.itemsList[i] == null) {
                Item.itemsList[i] = itemblock;
            }
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException | InstantiationException |
                 InvocationTargetException | NoSuchMethodException var4) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader RegisterBlock", var4);
            throwException(var4);
        }

    }

    public static void registerEntityID(Class class1, String s, int i) {
        try {
            EntityTypeAccessor.callRegister(class1, s, i);
        } catch (IllegalArgumentException var4) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader RegisterEntityID", var4);
            throwException(var4);
        }

    }

    public static void registerEntityID(Class class1, String s, int i, int j, int k) {
        registerEntityID(class1, s, i);
        EntityList.entityEggs.put(i, new EntityEggInfo(i, j, k));
    }

    public static void registerKey(BaseMod basemod, KeyBinding keybinding, boolean flag) {
        Map<KeyBinding, Boolean[]> obj = keyList.get(basemod);
        if (obj == null) {
            obj = new HashMap<>();
        }

        Boolean[] aflag = new Boolean[]{flag, false};
        obj.put(keybinding, aflag);
        keyList.put(basemod, obj);
    }

    public static void registerPacketChannel(BaseMod basemod, String s) {
        if (s.length() < 16) {
            packetChannels.put(s, basemod);
        } else {
            throw new RuntimeException(String.format("Invalid channel name: %s. Must be less than 16 characters.", s));
        }
    }

    public static void registerTileEntity(Class class1, String s) {
        registerTileEntity(class1, s, null);
    }

    public static void registerTileEntity(Class class1, String s, TileEntitySpecialRenderer tileentityspecialrenderer) {
        try {
            BlockEntityAccessor.callRegister(class1, s);
            if (tileentityspecialrenderer != null) {
                TileEntityRenderer tileentityrenderer = TileEntityRenderer.instance;
                ((BlockEntityRenderDispatcherAccessor) tileentityrenderer).getRenderers().put(class1, tileentityspecialrenderer);
                tileentityspecialrenderer.setTileEntityRenderer(tileentityrenderer);
            }
        } catch (IllegalArgumentException var5) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader RegisterTileEntity", var5);
            throwException(var5);
        }

    }

    public static void removeBiome(BiomeGenBase biomegenbase) {
        BiomeGenBase[] abiomegenbase = SetBaseBiomesLayerData.biomeArray;
        List<BiomeGenBase> list = Arrays.asList(abiomegenbase);
        ArrayList<BiomeGenBase> arraylist = new ArrayList<>(list);
        arraylist.remove(biomegenbase);

        SetBaseBiomesLayerData.biomeArray = arraylist.toArray(new BiomeGenBase[0]);
    }

    public static void removeSpawn(Class class1, EnumCreatureType enumcreaturetype) {
        removeSpawn(class1, enumcreaturetype, null);
    }

    public static void removeSpawn(Class class1, EnumCreatureType enumcreaturetype, BiomeGenBase[] abiomegenbase) {
        if (class1 == null) {
            throw new IllegalArgumentException("entityClass cannot be null");
        } else if (enumcreaturetype == null) {
            throw new IllegalArgumentException("spawnList cannot be null");
        } else {
            if (abiomegenbase == null) {
                abiomegenbase = standardBiomes;
            }

            for (BiomeGenBase BiomeGenBase : abiomegenbase) {
                List<SpawnListEntry> list = BiomeGenBase.getSpawnableList(enumcreaturetype);
                if (list != null) {

                    list.removeIf(spawnlistentry -> spawnlistentry.entityClass == class1);
                }
            }

        }
    }

    public static void removeSpawn(String s, EnumCreatureType enumcreaturetype) {
        removeSpawn(s, enumcreaturetype, null);
    }

    public static void removeSpawn(String s, EnumCreatureType enumcreaturetype, BiomeGenBase[] abiomegenbase) {
        Class<?> class1 = EntityTypeAccessor.getClassMap().get(s);
        if (class1 != null && EntityLiving.class.isAssignableFrom(class1)) {
            removeSpawn(class1, enumcreaturetype, abiomegenbase);
        }

    }

    public static boolean renderBlockIsItemFull3D(int i) {
        if (!blockSpecialInv.containsKey(i)) {
            return i == 35;
        } else {
            return blockSpecialInv.get(i);
        }
    }

    public static void renderInvBlock(RenderBlocks renderblocks, Block block, int i, int j) {
        BaseMod basemod = blockModels.get(j);
        if (basemod != null) {
            basemod.renderInvBlock(renderblocks, block, i, j);
        }
    }

    public static boolean renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l) {
        BaseMod basemod = blockModels.get(l);
        return basemod != null && basemod.renderWorldBlock(renderblocks, iblockaccess, i, j, k, block, l);
    }

    public static void saveConfig() throws IOException {
        cfgdir.mkdir();
        if (cfgfile.exists() || cfgfile.createNewFile()) {
            if (cfgfile.canWrite()) {
                FileOutputStream fileoutputstream = new FileOutputStream(cfgfile);
                props.store(fileoutputstream, "ModLoader Config");
                fileoutputstream.close();
            }

        }
    }

    public static void clientChat(String s) {
        for (BaseMod mod : modList) {
            mod.clientChat(s);
        }

    }

    public static void serverChat(NetServerHandler netserverhandler, String s) {
        for (BaseMod mod : modList) {
            mod.serverChat(netserverhandler, s);
        }

    }

    public static void clientConnect(NetClientHandler netclienthandler, Packet1Login packet1login) {
        clientHandler = netclienthandler;
        if (packetChannels.size() > 0) {
            Packet250CustomPayload packet250custompayload = new Packet250CustomPayload();
            packet250custompayload.channel = "REGISTER";
            StringBuilder stringbuilder = new StringBuilder();
            Iterator<String> iterator1 = packetChannels.keySet().iterator();
            stringbuilder.append(iterator1.next());

            while (iterator1.hasNext()) {
                stringbuilder.append("\u0000");
                stringbuilder.append(iterator1.next());
            }

            packet250custompayload.data = stringbuilder.toString().getBytes(StandardCharsets.UTF_8);
            packet250custompayload.length = packet250custompayload.data.length;
            clientSendPacket(packet250custompayload);
        }

        for (BaseMod mod : modList) {
            mod.clientConnect(netclienthandler);
        }

    }

    public static void clientDisconnect() {
        for (BaseMod mod : modList) {
            mod.clientDisconnect(clientHandler);
        }

        clientHandler = null;
    }

    public static void clientSendPacket(Packet packet) {
        if (clientHandler != null) {
            clientHandler.addToSendQueue(packet);
        }

    }

    public static void serverSendPacket(NetServerHandler serverHandler, Packet packet) {
        if (serverHandler != null) {
            serverHandler.sendPacketToPlayer(packet);
        }

    }

    public static void setInGameHook(BaseMod basemod, boolean flag, boolean flag1) {
        if (flag) {
            inGameHooks.put(basemod, flag1);
        } else {
            inGameHooks.remove(basemod);
        }

    }

    public static void setInGUIHook(BaseMod basemod, boolean flag, boolean flag1) {
        if (flag) {
            inGUIHooks.put(basemod, flag1);
        } else {
            inGUIHooks.remove(basemod);
        }

    }

    public static void setPrivateValue(Class class1, Object obj, int i, Object obj1) throws IllegalArgumentException, SecurityException {
        try {
            Field field = class1.getDeclaredFields()[i];
            field.setAccessible(true);

            if (field_modifiers == null) {
                field_modifiers = Field.class.getDeclaredField("modifiers");
                field_modifiers.setAccessible(true);
            }

            int j = field_modifiers.getInt(field);
            if ((j & 16) != 0) {
                field_modifiers.setInt(field, j & -17);
            }

            field.set(obj, obj1);
        } catch (IllegalAccessException | NoSuchFieldException var6) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader setPrivateValue", var6);
            throwException("An impossible error has occured!", var6);
        }

    }

    public static void setPrivateValue(Class class1, Object obj, String s, Object obj1) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        try {
            Field field = class1.getDeclaredField(s);

            if (field_modifiers == null) {
                field_modifiers = Field.class.getDeclaredField("modifiers");
                field_modifiers.setAccessible(true);
            }

            int i = field_modifiers.getInt(field);
            if ((i & 16) != 0) {
                field_modifiers.setInt(field, i & -17);
            }

            field.setAccessible(true);
            field.set(obj, obj1);
        } catch (IllegalAccessException var6) {
            Log.trace(Constants.MODLOADER_LOG_CATEGORY, "ModLoader setPrivateValue", var6);
            throwException("An impossible error has occured!", var6);
        }

    }

    private static void setupProperties(Class class1) throws IllegalArgumentException, IllegalAccessException, IOException, SecurityException {
        LinkedList<Field> linkedlist = new LinkedList<>();
        Properties properties = new Properties();
        int i = 0;
        int j = 0;
        File file = new File(cfgdir, class1.getSimpleName() + ".cfg");
        if (file.exists() && file.canRead()) {
            properties.load(Files.newInputStream(file.toPath()));
        }

        if (properties.containsKey("checksum")) {
            j = Integer.parseInt(properties.getProperty("checksum"), 36);
        }

        Field[] afield;
        int l = (afield = class1.getDeclaredFields()).length;

        for (int k = 0; k < l; ++k) {
            Field field = afield[k];
            if ((field.getModifiers() & 8) != 0 && field.isAnnotationPresent(MLProp.class)) {
                linkedlist.add(field);
                Object obj = field.get(null);
                i += obj.hashCode();
            }
        }

        StringBuilder stringbuilder = new StringBuilder();
        Iterator<Field> iterator = linkedlist.iterator();

        while (true) {
            MLProp mlprop;
            String s;
            Object obj1;
            Object obj2;
            double d;
            Field field1;
            do {
                do {
                    while (true) {
                        do {
                            do {
                                if (!iterator.hasNext()) {
                                    properties.put("checksum", Integer.toString(i, 36));
                                    if (!properties.isEmpty() && (file.exists() || file.createNewFile()) && file.canWrite()) {
                                        properties.store(Files.newOutputStream(file.toPath()), stringbuilder.toString());
                                    }

                                    return;
                                }

                                field1 = iterator.next();
                            } while ((field1.getModifiers() & 8) == 0);
                        } while (!field1.isAnnotationPresent(MLProp.class));

                        Class<?> class2 = field1.getType();
                        mlprop = field1.getAnnotation(MLProp.class);
                        s = mlprop.name().length() != 0 ? mlprop.name() : field1.getName();
                        obj1 = field1.get(null);
                        StringBuilder stringbuilder1 = new StringBuilder();
                        if (mlprop.min() != Double.NEGATIVE_INFINITY) {
                            stringbuilder1.append(String.format(",>=%.1f", mlprop.min()));
                        }

                        if (mlprop.max() != Double.POSITIVE_INFINITY) {
                            stringbuilder1.append(String.format(",<=%.1f", mlprop.max()));
                        }

                        StringBuilder stringbuilder2 = new StringBuilder();
                        if (mlprop.info().length() > 0) {
                            stringbuilder2.append(" -- ");
                            stringbuilder2.append(mlprop.info());
                        }

                        stringbuilder.append(String.format("%s (%s:%s%s)%s\n", s, class2.getName(), obj1, stringbuilder1, stringbuilder2));
                        if (j == i && properties.containsKey(s)) {
                            String s1 = properties.getProperty(s);
                            obj2 = null;
                            if (class2.isAssignableFrom(String.class)) {
                                obj2 = s1;
                            } else if (class2.isAssignableFrom(Integer.TYPE)) {
                                obj2 = Integer.parseInt(s1);
                            } else if (class2.isAssignableFrom(Short.TYPE)) {
                                obj2 = Short.parseShort(s1);
                            } else if (class2.isAssignableFrom(Byte.TYPE)) {
                                obj2 = Byte.parseByte(s1);
                            } else if (class2.isAssignableFrom(Boolean.TYPE)) {
                                obj2 = Boolean.parseBoolean(s1);
                            } else if (class2.isAssignableFrom(Float.TYPE)) {
                                obj2 = Float.parseFloat(s1);
                            } else if (class2.isAssignableFrom(Double.TYPE)) {
                                obj2 = Double.parseDouble(s1);
                            }
                            break;
                        }

                        Log.debug(Constants.MODLOADER_LOG_CATEGORY, s + " not in config, using default: " + obj1);
                        properties.setProperty(s, obj1.toString());
                    }
                } while (obj2 == null);

                if (!(obj2 instanceof Number)) {
                    break;
                }

                d = ((Number) obj2).doubleValue();
            } while (mlprop.min() != Double.NEGATIVE_INFINITY && d < mlprop.min() || mlprop.max() != Double.POSITIVE_INFINITY && d > mlprop.max());

            Log.debug(Constants.MODLOADER_LOG_CATEGORY, s + " set to " + obj2);
            if (!obj2.equals(obj1)) {
                field1.set(null, obj2);
            }
        }
    }

    private static void sortModList() throws Exception {
        HashMap<String, BaseMod> hashmap = new HashMap<>();

        for (BaseMod baseMod : (Iterable<BaseMod>) getLoadedMods()) {
            hashmap.put(baseMod.getClass().getSimpleName(), baseMod);
        }

        LinkedList<BaseMod> linkedlist = new LinkedList<>();
        int i = 0;

        label129:
        while (linkedlist.size() != modList.size() && i <= 10) {
            Iterator<BaseMod> iterator1 = modList.iterator();

            while (true) {
                BaseMod basemod1;
                int j;
                label125:
                while (true) {
                    String s;
                    do {
                        while (true) {
                            do {
                                if (!iterator1.hasNext()) {
                                    ++i;
                                    continue label129;
                                }

                                basemod1 = iterator1.next();
                            } while (linkedlist.contains(basemod1));

                            s = basemod1.getPriorities();
                            if (s != null && s.indexOf(58) != -1) {
                                break;
                            }

                            linkedlist.add(basemod1);
                        }
                    } while (i <= 0);

                    j = -1;
                    int k = Integer.MIN_VALUE;
                    int l = Integer.MAX_VALUE;
                    String[] as;
                    if (s.indexOf(59) > 0) {
                        as = s.split(";");
                    } else {
                        as = new String[]{s};
                    }

                    int i1 = 0;

                    while (true) {
                        if (i1 >= as.length) {
                            break label125;
                        }

                        String s1 = as[i1];
                        if (s1.indexOf(58) != -1) {
                            String[] as1 = s1.split(":");
                            String s2 = as1[0];
                            String s3 = as1[1];
                            if (s2.contentEquals("required-before") || s2.contentEquals("before") || s2.contentEquals("after") || s2.contentEquals("required-after")) {
                                if (s3.contentEquals("*")) {
                                    if (!s2.contentEquals("required-before") && !s2.contentEquals("before")) {
                                        if (s2.contentEquals("required-after") || s2.contentEquals("after")) {
                                            j = linkedlist.size();
                                        }
                                        break label125;
                                    }

                                    j = 0;
                                    break label125;
                                }

                                if ((s2.contentEquals("required-before") || s2.contentEquals("required-after")) && !hashmap.containsKey(s3)) {
                                    throw new Exception(String.format("%s is missing dependency: %s", basemod1, s3));
                                }

                                BaseMod basemod2 = hashmap.get(s3);
                                if (!linkedlist.contains(basemod2)) {
                                    break;
                                }

                                int j1 = linkedlist.indexOf(basemod2);
                                if (!s2.contentEquals("required-before") && !s2.contentEquals("before")) {
                                    if (s2.contentEquals("required-after") || s2.contentEquals("after")) {
                                        j = j1 + 1;
                                        if (j > k) {
                                            k = j;
                                        } else {
                                            j = k;
                                        }
                                    }
                                } else {
                                    j = j1;
                                    if (j1 < l) {
                                        l = j1;
                                    } else {
                                        j = l;
                                    }
                                }
                            }
                        }

                        ++i1;
                    }
                }

                if (j != -1) {
                    linkedlist.add(j, basemod1);
                }
            }
        }

        modList.clear();
        modList.addAll(linkedlist);
    }

    public static void takenFromCrafting(EntityPlayer entityplayer, ItemStack itemstack, IInventory iinventory) {
        for (BaseMod basemod : modList) {
            basemod.takenFromCrafting(entityplayer, itemstack, iinventory);
        }

    }

    public static void takenFromFurnace(EntityPlayer entityplayer, ItemStack itemstack) {
        for (BaseMod basemod : modList) {
            basemod.takenFromFurnace(entityplayer, itemstack);
        }

    }

    public static void throwException(String s, Throwable throwable) {
        Minecraft minecraft = getMinecraftInstance();
        if (minecraft != null) {
            minecraft.displayCrashReport(new CrashReport(s, throwable));
        } else {
            throw new RuntimeException(throwable);
        }
    }

    private static void throwException(Throwable throwable) {
        throwException("Exception occured in ModLoader", throwable);
    }

    public static String getCrashReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mods loaded: ");
        sb.append(getLoadedMods().size() + 1);
        sb.append('\n');
        sb.append("ModLoader 1.5.2");
        sb.append('\n');

        for (BaseMod mod : (List<BaseMod>) getLoadedMods()) {
            sb.append(mod.getName());
            sb.append(' ');
            sb.append(mod.getVersion());
            sb.append('\n');
        }

        return sb.toString();
    }

    public static void addCustomAnimationLogic(String name, TextureStitched tex) {
        customTextures.put(name, tex);
    }

    public static TextureStitched getCustomAnimationLogic(String name) {
        return customTextures.getOrDefault(name, null);
    }

    private ModLoader() {
    }

    static {
        cfgfile = new File(cfgdir, "ModLoader.cfg");
        cfgLoggingLevel = Level.FINER;
    }
}
