package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.client.renderer.item.ItemRendererPortableWorkbench;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemWorkbench;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapelessOreRecipe;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModComponentPortableWorkbench implements IModComponent {
    
    public ItemWorkbench itemWorkbench;
    
    protected IRecipe recipeWorkbench;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        MinecraftForge.EVENT_BUS.register(this);
        int durability = config.getInt("durability", this.getName(), 8192, 0, Short.MAX_VALUE,
                                       "\'0\' is infinite");
        this.itemWorkbench = new ItemWorkbench(durability);
        boolean craftingRecipePortableWorkbench =
                config.getBoolean("craftingRecipePortableWorkbench", this.getName(), true, "");
        if (craftingRecipePortableWorkbench) {
            this.recipeWorkbench = new ShapelessOreRecipe(
                    this.itemWorkbench,
                    "workbench",
                    "stickWood",
                    "stickWood").flow();
        }
    }
    
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(this.itemWorkbench);
    }
    
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> e) {
        if (this.recipeWorkbench != null) {
            e.getRegistry().register(this.recipeWorkbench);
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
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
