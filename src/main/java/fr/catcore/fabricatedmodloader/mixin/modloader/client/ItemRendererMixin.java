package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(RenderItem.class)
public abstract class ItemRendererMixin extends Render {

//    @Unique
//    private int cachedItemId = -1;
//
//    @Inject(
//            method = "render(Lnet/minecraft/entity/EntityItem;DDDFF)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
//                    remap = false
//            )
//    )
//    private void modLoader$renderfixPart1(EntityItem d, double e, double f, double g, float h, float par6, CallbackInfo ci) {
//        if (d.getEntityItem().id >= Block.blocksList.length) {
//            this.cachedItemId = d.getEntityItem().id;
//            d.getEntityItem().id = 0;
//        } else {
//            this.cachedItemId = -1;
//        }
//    }
//
//    @ModifyVariable(
//            method = "render(Lnet/minecraft/entity/EntityItem;DDDFF)V",
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
//            method = "render(Lnet/minecraft/entity/EntityItem;DDDFF)V",
//            at = @At(
//                    value = "FIELD",
//                    opcode = Opcodes.GETFIELD,
//                    target = "Lnet/minecraft/item/ItemStack;id:I",
//                    shift = At.Shift.AFTER
//            )
//    )
//    private void modLoader$renderfixPart2(EntityItem d, double e, double f, double g, float h, float par6, CallbackInfo ci) {
//        if (this.cachedItemId != -1 && this.cachedItemId >= Block.blocksList.length) {
//            d.getEntityItem().id = this.cachedItemId;
//        }
//    }


    @Shadow
    public boolean renderWithColor;
    @Shadow
    private Random random;
    @Shadow
    private RenderBlocks itemRenderBlocks;
    @Unique
    private int cachedItemId2 = -1;

    private ItemRendererMixin(Minecraft par1Minecraft) {
        super();
    }

    @Shadow
    protected abstract void renderDroppedItem(EntityItem itemEntity, Icon arg, int i, float f, float g, float h, float j);

    /**
     * @reason ModLoader patch
     * @author Risugami
     */
    /*@Overwrite
    public void doRenderItem(EntityItem itemEntity, double d, double e, double f, float g, float h) {
        this.random.setSeed(187L);
        ItemStack var10 = itemEntity.getEntityItem();
        if (var10.getItem() != null) {
            GL11.glPushMatrix();
            float var11 = MathHelper.sin(((float) itemEntity.age + h) / 10.0F + itemEntity.hoverStart) * 0.1F + 0.1F;
            float var12 = (((float) itemEntity.age + h) / 20.0F + itemEntity.hoverStart) * 57.295776F;
            byte var13 = 1;
            if (itemEntity.getEntityItem().stackSize > 1) {
                var13 = 2;
            }

            if (itemEntity.getEntityItem().stackSize > 5) {
                var13 = 3;
            }

            if (itemEntity.getEntityItem().stackSize > 20) {
                var13 = 4;
            }

            if (itemEntity.getEntityItem().stackSize > 40) {
                var13 = 5;
            }

            GL11.glTranslatef((float) d, (float) e + var11, (float) f);
            GL11.glEnable(32826);

            Block var28 = null;
            if (var10.itemID < Block.blocksList.length) {
                var28 = Block.blocksList[var10.itemID];
            }

            int var17;
            float var18;
            float var19;
            float var20;
            if (var10.getItemSpriteNumber() == 0 && var28 != null && RenderBlocks.doesRenderIDRenderItemIn3D(var28.getRenderType())) {
                GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
                if (RenderItem.renderInFrame) {
                    GL11.glScalef(1.25F, 1.25F, 1.25F);
                    GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                this.loadTexture("/terrain.png");
                float var24 = 0.25F;
                int var25 = var28.getRenderType();
                if (var25 == 1 || var25 == 19 || var25 == 12 || var25 == 2) {
                    var24 = 0.5F;
                }

                GL11.glScalef(var24, var24, var24);

                for (var17 = 0; var17 < var13; ++var17) {
                    GL11.glPushMatrix();
                    if (var17 > 0) {
                        var18 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
                        var19 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
                        var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
                        GL11.glTranslatef(var18, var19, var20);
                    }

                    var18 = 1.0F;
                    this.itemRenderBlocks.renderBlockAsItem(var28, var10.getItemDamage(), var18);
                    GL11.glPopMatrix();
                }
            } else {
                float var16;
                if (var10.getItem().requiresMultipleRenderPasses()) {
                    if (RenderItem.renderInFrame) {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    } else {
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                    }

                    this.loadTexture("/gui/items.png");

                    for (int var14 = 0; var14 <= 1; ++var14) {
                        this.random.setSeed(187L);
                        Icon var15 = var10.getItem().getIconFromDamageForRenderPass(var10.getItemDamage(), var14);
                        var16 = 1.0F;
                        if (this.renderWithColor) {
                            var17 = Item.itemsList[var10.itemID].getColorFromItemStack(var10, var14);
                            var18 = (float) (var17 >> 16 & 255) / 255.0F;
                            var19 = (float) (var17 >> 8 & 255) / 255.0F;
                            var20 = (float) (var17 & 255) / 255.0F;
                            GL11.glColor4f(var18 * var16, var19 * var16, var20 * var16, 1.0F);
                            this.renderDroppedItem(itemEntity, var15, var13, h, var18 * var16, var19 * var16, var20 * var16);
                        } else {
                            this.renderDroppedItem(itemEntity, var15, var13, h, 1.0F, 1.0F, 1.0F);
                        }
                    }
                } else {
                    if (RenderItem.renderInFrame) {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    } else {
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                    }

                    Icon var21 = var10.getIconIndex();
                    if (var10.getItemSpriteNumber() == 0) {
                        this.loadTexture("/terrain.png");
                    } else {
                        this.loadTexture("/gui/items.png");
                    }

                    if (this.renderWithColor) {
                        int var23 = Item.itemsList[var10.itemID].getColorFromItemStack(var10, 0);
                        var16 = (float) (var23 >> 16 & 255) / 255.0F;
                        float var26 = (float) (var23 >> 8 & 255) / 255.0F;
                        var18 = (float) (var23 & 255) / 255.0F;
                        var19 = 1.0F;
                        this.renderDroppedItem(itemEntity, var21, var13, h, var16 * var19, var26 * var19, var18 * var19);
                    } else {
                        this.renderDroppedItem(itemEntity, var21, var13, h, 1.0F, 1.0F, 1.0F);
                    }
                }
            }

            GL11.glDisable(32826);
            GL11.glPopMatrix();
        }
    }*/

    @Inject(method = "renderDroppedItem", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 1, remap = false))
    private void modLoader$otherRenderFix1(EntityItem arg, Icon i, int f, float g, float h, float j, float par7, CallbackInfo ci) {
        if (arg.getEntityItem().getItemSpriteNumber() == 0 && arg.getEntityItem().itemID >= Block.blocksList.length) {
            this.cachedItemId2 = arg.getEntityItem().itemID;
            arg.getEntityItem().itemID = 0;
        }
    }

    @ModifyArg(method = "renderDroppedItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderItem;loadTexture(Ljava/lang/String;)V", ordinal = 0)
    )
    private String modLoader$otherRenderFix2(String par1) {
        if (this.cachedItemId2 != -1) par1 = "/gui/items.png";

        return par1;
    }

    @Inject(method = "renderDroppedItem",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0, remap = false)
    )
    private void modLoader$otherRenderFix3(EntityItem arg, Icon i, int f, float g, float h, float j, float par7, CallbackInfo ci) {
        if (this.cachedItemId2 != -1) {
            arg.getEntityItem().itemID = this.cachedItemId2;
            this.cachedItemId2 = -1;
        }
    }
}
