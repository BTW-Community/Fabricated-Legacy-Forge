package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public abstract class HeldItemRendererMixin {

//    @Unique
//    private int cachedItemId = -1;
//
//    @Inject(
//            method = "method_1357",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
//                    remap = false,
//                    ordinal = 0
//            )
//    )
//    private void modLoader$renderfixPart1(EntityLiving mobEntity, ItemStack itemStack, int i, CallbackInfo ci) {
//        if (itemStack.itemID >= Block.blocksList.length) {
//            this.cachedItemId = itemStack.itemID;
//            itemStack.itemID = 0;
//        } else {
//            this.cachedItemId = -1;
//        }
//    }
//
//    @ModifyVariable(
//            method = "method_1357",
//            at = @At("STORE")
//    )
//    private Block modLoader$renderfixPart2(Block value) {
//        if (this.cachedItemId != -1 && this.cachedItemId >= Block.blocksList.length) {
//            return null;
//        }
//        return value;
//    }
//
//    @Inject(
//            method = "method_1357",
//            at = @At(
//                    value = "FIELD",
//                    opcode = Opcodes.GETFIELD,
//                    target = "Lnet/minecraft/item/ItemStack;id:I",
//                    shift = At.Shift.AFTER,
//                    ordinal = 0
//            )
//    )
//    private void modLoader$renderfixPart2(EntityLiving mobEntity, ItemStack itemStack, int i, CallbackInfo ci) {
//        if (this.cachedItemId != -1 && this.cachedItemId >= Block.blocksList.length) {
//            itemStack.itemID = this.cachedItemId;
//        }
//    }

    @Shadow
    private Minecraft mc;

    @Shadow
    private RenderBlocks renderBlocksInstance;


    /**
     * @reason ModLoader patch
     * @author Risugami
     */
    /*@Overwrite
    public void renderItem(EntityLiving mobEntity, ItemStack itemStack, int i) {
        GL11.glPushMatrix();
        Block block = null;
        if (itemStack.itemID < Block.blocksList.length) {
            block = Block.blocksList[itemStack.itemID];
        }

        if (itemStack.getItemSpriteNumber() == 0 && block != null && RenderBlocks.doesRenderIDRenderItemIn3D(block.getRenderType())) {
            this.mc.renderEngine.bindTexture("/terrain.png");
            this.renderBlocksInstance.renderBlockAsItem(Block.blocksList[itemStack.itemID], itemStack.getItemDamage(), 1.0F);
        } else {
            Icon var4 = mobEntity.getItemIcon(itemStack, i);
            if (var4 == null) {
                GL11.glPopMatrix();
                return;
            }

            if (itemStack.getItemSpriteNumber() == 0) {
                this.mc.renderEngine.bindTexture("/terrain.png");
            } else {
                this.mc.renderEngine.bindTexture("/gui/items.png");
            }

            Tessellator var5 = Tessellator.instance;
            float var6 = var4.getMinU();
            float var7 = var4.getMaxU();
            float var8 = var4.getMinV();
            float var9 = var4.getMaxV();
            float var10 = 0.0F;
            float var11 = 0.3F;
            GL11.glEnable(32826);
            GL11.glTranslatef(-var10, -var11, 0.0F);
            float var12 = 1.5F;
            GL11.glScalef(var12, var12, var12);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            ItemRenderer.renderItemIn2D(var5, var7, var8, var6, var9, var4.getSheetWidth(), var4.getSheetHeight(), 0.0625F);
            if (itemStack != null && itemStack.isItemEnchanted() && i == 0) {
                GL11.glDepthFunc(514);
                GL11.glDisable(2896);
                this.mc.renderEngine.bindTexture("%blur%/misc/glint.png");
                GL11.glEnable(3042);
                GL11.glBlendFunc(768, 1);
                float var13 = 0.76F;
                GL11.glColor4f(0.5F * var13, 0.25F * var13, 0.8F * var13, 1.0F);
                GL11.glMatrixMode(5890);
                GL11.glPushMatrix();
                float var14 = 0.125F;
                GL11.glScalef(var14, var14, var14);
                float var15 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(var15, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(var5, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(var14, var14, var14);
                var15 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-var15, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(var5, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(5888);
                GL11.glDisable(3042);
                GL11.glEnable(2896);
                GL11.glDepthFunc(515);
            }

            GL11.glDisable(32826);
        }

        GL11.glPopMatrix();
    }*/
}
