package com.vanym.paniclecraft.core.component;

import java.util.ArrayList;
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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModComponentAdvSign implements IModComponent {
    
    public ItemAdvSign itemAdvSign;
    public BlockAdvSign blockAdvSign;
    
    protected List<IRecipe> recipes = new ArrayList<>();
    
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
        MinecraftForge.EVENT_BUS.register(this);
        this.blockAdvSign = new BlockAdvSign();
        this.itemAdvSign = new ItemAdvSign();
        GameRegistry.registerTileEntity(TileEntityAdvSign.class, TileEntityAdvSign.ID);
        boolean craftingRecipeEasy = config.getBoolean("craftingRecipeEasy", this.getName(), true,
                                                       "crafting using just one regular sign");
        if (craftingRecipeEasy) {
            ShapelessOreRecipe recipe = new ShapelessOreRecipe(this.itemAdvSign, Items.SIGN);
            recipe.setRegistryName(DEF.MOD_ID, "advSignEasy");
            this.recipes.add(recipe);
        }
        boolean craftingRecipeBook =
                config.getBoolean("craftingRecipeWithBook", this.getName(), false,
                                  "crafting using book and regular sign");
        if (craftingRecipeBook) {
            ShapelessOreRecipe recipe =
                    new ShapelessOreRecipe(this.itemAdvSign, Items.SIGN, Items.BOOK);
            recipe.setRegistryName(DEF.MOD_ID, "advSignBook");
            this.recipes.add(recipe);
        }
        boolean craftingRecipeClear =
                config.getBoolean("craftingRecipeClear", this.getName(), true,
                                  "clear adv sign using crafting");
        if (craftingRecipeClear) {
            ShapelessOreRecipe recipe = new ShapelessOreRecipe(this.itemAdvSign, this.itemAdvSign);
            recipe.setRegistryName(DEF.MOD_ID, "advSignClear");
            this.recipes.add(recipe);
        }
        
        Core.instance.command.addSubCommand(new CommandAdvSign());
        
        Core.instance.network.registerMessage(MessageAdvSignChange.class,
                                              MessageAdvSignChange.class, 20,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessageAdvSignOpenGui.class,
                                              MessageAdvSignOpenGui.class, 21,
                                              Side.CLIENT);
    }
    
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().register(this.blockAdvSign);
    }
    
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(this.itemAdvSign);
    }
    
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> e) {
        this.recipes.forEach(e.getRegistry()::register);
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
