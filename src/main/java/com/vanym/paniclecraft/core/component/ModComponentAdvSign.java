package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererAdvSign;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityAdvSignRenderer;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

public class ModComponentAdvSign implements ModComponent {
    
    public ItemAdvSign itemAdvSign;
    public BlockAdvSign blockAdvSign;
    
    @SideOnly(Side.CLIENT)
    public TileEntityAdvSignRenderer tileAdvSignRenderer = new TileEntityAdvSignRenderer();
    
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
        GameRegistry.registerTileEntity(TileEntityAdvSign.class, DEF.MOD_ID + ".advSign");
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
                config.getBoolean("craftingRecipeClear", "AdvSign", true,
                                  "clear adv sign using crafting");
        if (craftingRecipeClear) {
            GameRegistry.addShapelessRecipe(new ItemStack(this.itemAdvSign, 1), this.itemAdvSign);
        }
        
        Core.instance.network.registerMessage(MessageAdvSignChange.class,
                                              MessageAdvSignChange.class, 20,
                                              Side.SERVER);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        boolean advSignTile = config.getBoolean("advSignTile", CLIENT_RENDER, true, "");
        if (advSignTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvSign.class,
                                                         this.tileAdvSignRenderer);
        }
        boolean advSignItem = config.getBoolean("advSignItem", CLIENT_RENDER, true, "");
        if (advSignItem) {
            MinecraftForgeClient.registerItemRenderer(this.itemAdvSign, new ItemRendererAdvSign());
        }
    }
    
    @Override
    public String getName() {
        return "advSign";
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
