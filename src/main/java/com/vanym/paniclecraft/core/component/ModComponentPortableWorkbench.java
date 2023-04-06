package com.vanym.paniclecraft.core.component;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPortableWorkbench;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemWorkbench;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModComponentPortableWorkbench extends ModComponent {
    
    @ModComponentObject
    public ItemWorkbench itemWorkbench;
    
    @SideOnly(Side.CLIENT)
    public boolean renderWorkbenchItem;
    
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
            GameRegistry.addRecipe(new ShapelessOreRecipe(
                    this.itemWorkbench,
                    "craftingTableWood",
                    "stickWood",
                    "stickWood"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(
                    this.itemWorkbench,
                    Blocks.crafting_table,
                    "stickWood",
                    "stickWood"));
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        MinecraftForgeClient.registerItemRenderer(this.itemWorkbench,
                                                  new ItemRendererPortableWorkbench());
        this.configChangedClient(config);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void configChangedClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        config.restartless();
        this.renderWorkbenchItem = config.getBoolean("portableWorkbenchItem", CLIENT_RENDER, true,
                                                     "fancy renderer for portable workbench");
        config.restartlessReset();
    }
    
    @Override
    public String getName() {
        return "portableworkbench";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
