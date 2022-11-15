package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import net.minecraft.src.CraftingManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ShapedRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CraftingManager.class)
public interface RecipeDispatcherAccessor {

    @Invoker("addRecipe")
    ShapedRecipes registerShapedRecipe_invoker(ItemStack itemStack, Object... objects);

    @Invoker("addShapelessRecipe")
    void registerShapelessRecipe_invoker(ItemStack result, Object... args);
}
