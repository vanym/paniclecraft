package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemBroom;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModComponentBroom implements ModComponent {
    
    public ItemBroom itemBroom;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        int durability = config.getInt("durability", this.getName(), 3072, 0, Integer.MAX_VALUE,
                                       "0 is infinite");
        double radius = config.get(this.getName(), "radius", 6.0D).getDouble(6.0D);
        this.itemBroom = new ItemBroom(durability, radius);
        boolean craftingRecipe =
                config.getBoolean("craftingRecipeBroom", this.getName(), true, ENABLE_FLAG);
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
    
    @Override
    public List<Item> getItems() {
        return Arrays.asList(this.itemBroom);
    }
}
