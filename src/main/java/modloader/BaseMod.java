package modloader;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import java.util.Map;
import java.util.Random;


@SuppressWarnings("unused")
public abstract class BaseMod {
    public BaseMod() {
    }

    public int addFuel(int i, int j) {
        return 0;
    }

    public void addRenderer(Map map) {
    }

    public void generateNether(World world, Random random, int i, int j) {
    }

    public void generateSurface(World world, Random random, int i, int j) {
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public String getPriorities() {
        return "";
    }

    public abstract String getVersion();

    public void keyboardEvent(KeyBinding keybinding) {
    }

    public abstract void load();

    public void modsLoaded() {
    }

    public void onItemPickup(EntityPlayer entityplayer, ItemStack itemstack) {
    }

    public boolean onTickInGame(float f, Minecraft minecraft) {
        return false;
    }

    public boolean onTickInGUI(float f, Minecraft minecraft, GuiScreen guiscreen) {
        return false;
    }

    public void clientChat(String s) {
    }

    public void serverChat(NetServerHandler netserverhandler, String s) {
    }

    public void clientCustomPayload(NetClientHandler clientHandler, Packet250CustomPayload packet250custompayload) {
    }

    public void serverCustomPayload(NetServerHandler serverHandler, Packet250CustomPayload packet250custompayload) {
    }

    public void registerAnimation(Minecraft minecraft) {
    }

    public void renderInvBlock(RenderBlocks renderblocks, Block block, int i, int j) {
    }

    public boolean renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l) {
        return false;
    }

    public void clientConnect(NetClientHandler netclienthandler) {
    }

    public void clientDisconnect(NetClientHandler clientHandler) {
    }

    public void takenFromCrafting(EntityPlayer entityplayer, ItemStack itemstack, IInventory iinventory) {
    }

    public void takenFromFurnace(EntityPlayer entityplayer, ItemStack itemstack) {
    }

    public String toString() {
        return this.getName() + ' ' + this.getVersion();
    }

    public GuiContainer getContainerGUI(EntityClientPlayerMP player, int inventoryType, int x, int y, int z) {
        return null;
    }

    public Entity spawnEntity(int id, World world, double x, double y, double z) {
        return null;
    }

    public Packet23VehicleSpawn getSpawnPacket(Entity entity, int id) {
        return null;
    }
}
