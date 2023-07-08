package com.vanym.paniclecraft.plugins.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.container.ContainerPortableWorkbench;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIMod3Config implements IModPlugin {
    
    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        if (Core.instance.painting.isEnabled()) {
            this.registerPaintingRecipes(registry);
        }
    }
    
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
        if (Core.instance.portableworkbench.isEnabled()) {
            this.registerPortableWorkbenchRecipeTransferHandlers(registry);
        }
    }
    
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (Core.instance.portableworkbench.isEnabled()) {
            this.registerPortableWorkbenchRecipeCatalysts(registration);
        }
    }
    
    protected void registerPaintingRecipes(IRecipeRegistration registry) {
        ItemStack painting = new ItemStack(Core.instance.painting.itemPainting);
        List<ItemStack> left = Arrays.asList(painting);
        List<ItemStack> right = Collections.nCopies(left.size(), painting);
        List<ItemStack> output = left.stream()
                                     .map(ItemStack::copy)
                                     .peek(stack->stack.setCount(2))
                                     .collect(Collectors.toList());
        Object recipe = registry.getVanillaRecipeFactory()
                                .createAnvilRecipe(left, right, output);
        registry.addRecipes(Arrays.asList(recipe), VanillaRecipeCategoryUid.ANVIL);
    }
    
    protected void registerPortableWorkbenchRecipeTransferHandlers(
            IRecipeTransferRegistration registry) {
        registry.addRecipeTransferHandler(ContainerPortableWorkbench.class,
                                          VanillaRecipeCategoryUid.CRAFTING,
                                          1, 9, 10, 36);
    }
    
    protected void registerPortableWorkbenchRecipeCatalysts(
            IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(Core.instance.portableworkbench.itemWorkbench),
                                   VanillaRecipeCategoryUid.CRAFTING);
    }
    
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DEF.MOD_ID, "jei");
    }
}
