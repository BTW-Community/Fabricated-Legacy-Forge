package fr.catcore.fabricatedmodloader.mixin.compat.toomanyitems;

import net.minecraft.TMI;
import net.minecraft.TMIConfig;
import net.minecraft.TMIPrivateFields;
import net.minecraft.TMIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GuiContainer.class)
public class GuiContainerTMICompatMixin extends GuiScreen {
    @Shadow protected int guiLeft;

    @Shadow protected int guiTop;

    @Shadow protected int xSize;

    @Shadow protected int ySize;

    @Shadow public Container inventorySlots;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectConstructor(Container par1Container, CallbackInfo ci) {
        TMI.instance.controller.onCreate(this_());
    }

    private GuiContainer this_() {
        return (GuiContainer) (Object) this;
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int var1 = Mouse.getEventX() * this.width / Minecraft.getMinecraft().displayWidth;
        int var2 = this.height - Mouse.getEventY() * this.height / Minecraft.getMinecraft().displayHeight - 1;
        TMI.instance.controller.handleScrollWheel(var1, var2);
    }



    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void injectDrawScreen(int par1, int par2, float par3, CallbackInfo ci) {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        if (this_() instanceof GuiContainerCreative) {
            try {
                GuiTextField var4 = (GuiTextField) TMIPrivateFields.creativeSearchBox.get(this);
                TMIPrivateFields.textFieldX.setInt(var4, this.guiLeft + 82);
            } catch (Exception var15) {
                var15.printStackTrace();
            }
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiContainer;drawGuiContainerForegroundLayer(II)V"))
    private void injectDrawScreen2(int par1, int par2, float par3, CallbackInfo ci) {
        GL11.glTranslatef((float)(-guiLeft), (float)(-guiTop), 0.0F);
        TMI.instance.controller.onEnterFrame(par1, par2, this.xSize, this.ySize);
        GL11.glTranslatef((float)guiLeft, (float)guiTop, 0.0F);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Redirect(method = "drawItemStackTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;getTooltip(Lnet/minecraft/src/EntityPlayer;Z)Ljava/util/List;"))
    private List redirectGetTooltip(ItemStack itemStack, EntityPlayer par1EntityPlayer, boolean par2) {
        return TMIUtils.itemDisplayNameMultiline(itemStack, TMIConfig.getInstance().isEnabled(), Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
    }

    @Inject(method = "mouseClicked",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSystemTime()J"),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectMouseClicked(int i, int j, int k, CallbackInfo ci, boolean var4, Slot var5) {
        boolean bool = i >= this.guiLeft && j >= this.guiTop && i <= this.guiLeft + this.xSize && j <= this.guiTop + this.ySize;
        if (!TMI.instance.controller.onMouseEvent(i, j, k, bool, var5, this.inventorySlots)) {
            ci.cancel();
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void injectKeyTyped(char c, int i, CallbackInfo ci) {
        if (TMI.instance.controller.onKeypress(c, i)) {
            ci.cancel();
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void injectOnGuiClosed(CallbackInfo ci) {
        TMI.instance.controller.onClose();
    }

    @Inject(method = "doesGuiPauseGame", at = @At("HEAD"))
    private void injectDoesGuiPauseGame(CallbackInfoReturnable<Boolean> cir) {
        TMI.instance.controller.shouldPauseGame();
    }
}
