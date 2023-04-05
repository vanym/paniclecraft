package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPortableWorkbench;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemWorkbench;
import com.vanym.paniclecraft.recipe.RecipeRegister;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapelessOreRecipe;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModComponentPortableWorkbench implements ModComponent {
    
    public ItemWorkbench itemWorkbench;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        int durability = config.getInt("durability", this.getName(), 8192, 0, Short.MAX_VALUE,
                                       "\'0\' is infinite");
        this.itemWorkbench = new ItemWorkbench(durability);
        Core.instance.registerItem(this.itemWorkbench);
        boolean craftingRecipePortableWorkbench =
                config.getBoolean("craftingRecipePortableWorkbench", this.getName(), true, "");
        if (craftingRecipePortableWorkbench) {
            RecipeRegister.register(new ShapelessOreRecipe(
                    this.itemWorkbench,
                    "workbench",
                    "stickWood",
                    "stickWood"));
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        this.itemWorkbench.setTileEntityItemStackRenderer(new ItemRendererPortableWorkbench());
    }
    
    @Override
    public String getName() {
        return "portableworkbench";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public List<Item> getItems() {
        return Arrays.asList(this.itemWorkbench);
    }
}
