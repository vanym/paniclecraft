package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemBroom;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapedOreRecipe;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModComponentBroom implements ModComponent {
    
    public ItemBroom itemBroom;
    
    protected IRecipe recipeBroom;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        MinecraftForge.EVENT_BUS.register(this);
        int durability = config.getInt("durability", this.getName(), 3072, 0, Short.MAX_VALUE,
                                       "\'0\' is infinite");
        double radius = config.getDouble("radius", this.getName(), 6.0D, 1.0D, 64.0D, "");
        this.itemBroom = new ItemBroom(durability, radius);
        boolean craftingRecipe = config.getBoolean("craftingRecipeBroom", this.getName(), true, "");
        if (craftingRecipe) {
            this.recipeBroom = new ShapedOreRecipe(
                    this.itemBroom,
                    "  1",
                    "12 ",
                    "11 ",
                    Character.valueOf('1'),
                    "stickWood",
                    Character.valueOf('2'),
                    "string").flow();
        }
    }
    
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(this.itemBroom);
    }
    
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> e) {
        if (this.recipeBroom != null) {
            e.getRegistry().register(this.recipeBroom);
        }
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
