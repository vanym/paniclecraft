package com.vanym.paniclecraft.core.component;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemBroom;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModComponentBroom extends ModComponent {
    
    @ModComponentObject
    public ItemBroom itemBroom;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        int durability = config.getInt("durability", this.getName(), 3072, 0, Short.MAX_VALUE,
                                       "\'0\' is infinite");
        double radius = config.getDouble("radius", this.getName(), 6.0D, 1.0D, 64.0D, "");
        this.itemBroom = new ItemBroom(durability, radius);
        boolean craftingRecipe = config.getBoolean("craftingRecipeBroom", this.getName(), true, "");
        if (craftingRecipe) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemBroom,
                    "001",
                    "120",
                    "110",
                    Character.valueOf('1'),
                    "stickWood",
                    Character.valueOf('2'),
                    Items.string);
            GameRegistry.addRecipe(recipe);
        }
        Core.instance.registerItem(this.itemBroom);
    }
    
    @Override
    public String getName() {
        return "broom";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
