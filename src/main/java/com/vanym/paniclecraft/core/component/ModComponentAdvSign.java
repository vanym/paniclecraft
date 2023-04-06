package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererAdvSign;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityAdvSignRenderer;
import com.vanym.paniclecraft.command.CommandAdvSign;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.network.message.MessageAdvSignOpenGui;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

public class ModComponentAdvSign implements IModComponent {
    
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
        GameRegistry.registerBlock(this.blockAdvSign, null, this.blockAdvSign.getName());
        Core.instance.registerItem(this.itemAdvSign);
        GameRegistry.registerTileEntity(TileEntityAdvSign.class, TileEntityAdvSign.ID.toString());
        boolean craftingRecipeEasy = config.getBoolean("craftingRecipeEasy", this.getName(), true,
                                                       "crafting using just one regular sign");
        if (craftingRecipeEasy) {
            GameRegistry.addShapelessRecipe(new ItemStack(this.itemAdvSign, 1), Items.sign);
        }
        boolean craftingRecipeBook =
                config.getBoolean("craftingRecipeWithBook", this.getName(), false,
                                  "crafting using book and regular sign");
        if (craftingRecipeBook) {
            GameRegistry.addShapelessRecipe(new ItemStack(this.itemAdvSign, 1), Items.sign,
                                            Items.book);
        }
        boolean craftingRecipeClear =
                config.getBoolean("craftingRecipeClear", this.getName(), true,
                                  "clear adv sign using crafting");
        if (craftingRecipeClear) {
            GameRegistry.addShapelessRecipe(new ItemStack(this.itemAdvSign, 1), this.itemAdvSign);
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
        this.tileAdvSignRenderer.func_147497_a(TileEntityRendererDispatcher.instance);
        this.itemAdvSignRenderer = new ItemRendererAdvSign();
        MinecraftForgeClient.registerItemRenderer(this.itemAdvSign, this.itemAdvSignRenderer);
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
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(TileEntityAdvSign.class,
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
