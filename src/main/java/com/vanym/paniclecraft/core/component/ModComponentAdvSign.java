package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererAdvSign;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityAdvSignRenderer;
import com.vanym.paniclecraft.command.CommandAdvSign;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.network.message.MessageAdvSignOpenGui;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapelessOreRecipe;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModComponentAdvSign implements ModComponent {
    
    public ItemAdvSign itemAdvSign;
    public BlockAdvSign blockAdvSign;
    
    @SideOnly(Side.CLIENT)
    public TileEntityAdvSignRenderer tileAdvSignRenderer;
    @SideOnly(Side.CLIENT)
    public ItemRendererAdvSign itemAdvSignRenderer;
    
    @SideOnly(Side.CLIENT)
    public boolean renderAdvSignItem;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        this.blockAdvSign = new BlockAdvSign();
        this.itemAdvSign = new ItemAdvSign();
        ForgeRegistries.BLOCKS.register(this.blockAdvSign);
        Core.instance.registerItem(this.itemAdvSign);
        GameRegistry.registerTileEntity(TileEntityAdvSign.class,
                                        new ResourceLocation(DEF.MOD_ID, "advSign"));
        boolean craftingRecipeEasy = config.getBoolean("craftingRecipeEasy", this.getName(), true,
                                                       "crafting using just one regular sign");
        if (craftingRecipeEasy) {
            ShapelessOreRecipe recipe = new ShapelessOreRecipe(this.itemAdvSign, Items.SIGN);
            recipe.setRegistryName(DEF.MOD_ID, "advSignEasy");
            ForgeRegistries.RECIPES.register(recipe);
        }
        boolean craftingRecipeBook =
                config.getBoolean("craftingRecipeWithBook", this.getName(), false,
                                  "crafting using book and regular sign");
        if (craftingRecipeBook) {
            ShapelessOreRecipe recipe =
                    new ShapelessOreRecipe(this.itemAdvSign, Items.SIGN, Items.BOOK);
            recipe.setRegistryName(DEF.MOD_ID, "advSignBook");
            ForgeRegistries.RECIPES.register(recipe);
        }
        boolean craftingRecipeClear =
                config.getBoolean("craftingRecipeClear", this.getName(), true,
                                  "clear adv sign using crafting");
        if (craftingRecipeClear) {
            ShapelessOreRecipe recipe = new ShapelessOreRecipe(this.itemAdvSign, this.itemAdvSign);
            recipe.setRegistryName(DEF.MOD_ID, "advSignClear");
            ForgeRegistries.RECIPES.register(recipe);
        }
        
        Core.instance.command.addSubCommand(new CommandAdvSign());
        
        Core.instance.network.registerMessage(MessageAdvSignChange.class,
                                              MessageAdvSignChange.class, 20,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessageAdvSignOpenGui.class,
                                              MessageAdvSignOpenGui.class, 21,
                                              Side.CLIENT);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.tileAdvSignRenderer = new TileEntityAdvSignRenderer();
        this.tileAdvSignRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        this.itemAdvSignRenderer = new ItemRendererAdvSign();
        this.itemAdvSign.setTileEntityItemStackRenderer(this.itemAdvSignRenderer);
        this.configChangedClient(config);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void configChangedClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        config.restartless();
        this.renderAdvSignItem = config.getBoolean("advSignItem", CLIENT_RENDER, true, "");
        boolean advSignTile = config.getBoolean("advSignTile", CLIENT_RENDER, true, "");
        if (advSignTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvSign.class,
                                                         this.tileAdvSignRenderer);
        } else {
            TileEntityRendererDispatcher.instance.renderers.remove(TileEntityAdvSign.class,
                                                                   this.tileAdvSignRenderer);
        }
        config.restartlessReset();
    }
    
    @Override
    public String getName() {
        return "advsign";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public List<Item> getItems() {
        return Arrays.asList(this.itemAdvSign);
    }
}
