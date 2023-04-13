package com.vanym.paniclecraft.plugins.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.gui.container.GuiPortableCrafting;
import com.vanym.paniclecraft.container.ContainerPortableWorkbench;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEIMod3Config implements IModPlugin {
    
    @Override
    public void register(IModRegistry registry) {
        if (Core.instance.painting.isEnabled()) {
            this.registerPainting(registry);
        }
        if (Core.instance.portableworkbench.isEnabled()) {
            this.registerPortableWorkbench(registry);
        }
    }
    
    protected void registerPainting(IModRegistry registry) {
        ItemStack painting = new ItemStack(Core.instance.painting.itemPainting);
        List<ItemStack> left = Arrays.asList(painting);
        List<ItemStack> right = Collections.nCopies(left.size(), painting);
        List<ItemStack> output = left.stream()
                                     .map(ItemStack::copy)
                                     .peek(stack->stack.setCount(2))
                                     .collect(Collectors.toList());
        IRecipeWrapper recipe = registry.getJeiHelpers()
                                        .getVanillaRecipeFactory()
                                        .createAnvilRecipe(left, right, output);
        registry.addRecipes(Arrays.asList(recipe), VanillaRecipeCategoryUid.ANVIL);
    }
    
    protected void registerPortableWorkbench(IModRegistry registry) {
        registry.getRecipeTransferRegistry()
                .addRecipeTransferHandler(ContainerPortableWorkbench.class,
                                          VanillaRecipeCategoryUid.CRAFTING,
                                          1, 9, 10, 36);
        registry.addRecipeClickArea(GuiPortableCrafting.class, 88, 32, 28, 23,
                                    VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipeCatalyst(new ItemStack(Core.instance.portableworkbench.itemWorkbench),
                                   VanillaRecipeCategoryUid.CRAFTING);
    }
}
