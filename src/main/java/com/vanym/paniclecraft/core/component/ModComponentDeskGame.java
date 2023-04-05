package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockChessDesk;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererChessDesk;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityChessDeskRenderer;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.recipe.RecipeRegister;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapedOreRecipe;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapelessOreRecipe;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModComponentDeskGame implements ModComponent {
    
    public BlockChessDesk blockChessDesk;
    public ItemChessDesk itemChessDesk;
    
    @SideOnly(Side.CLIENT)
    public TileEntityChessDeskRenderer tileChessDeskRenderer;
    @SideOnly(Side.CLIENT)
    public ItemRendererChessDesk itemChessDeskRenderer;
    
    @SideOnly(Side.CLIENT)
    public boolean renderChessDeskItem;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        this.blockChessDesk = new BlockChessDesk();
        ForgeRegistries.BLOCKS.register(this.blockChessDesk);
        this.itemChessDesk = new ItemChessDesk(this.blockChessDesk);
        Core.instance.registerItem(this.itemChessDesk);
        GameRegistry.registerTileEntity(TileEntityChessDesk.class, TileEntityChessDesk.ID);
        boolean craftingRecipeChessDesk =
                config.getBoolean("craftingRecipeChessDesk", this.getName(), true, "");
        if (craftingRecipeChessDesk) {
            RecipeRegister.register(new ShapedOreRecipe(
                    this.itemChessDesk,
                    true,
                    "w b",
                    "ppp",
                    Character.valueOf('w'),
                    "woolWhite",
                    Character.valueOf('b'),
                    "woolBlack",
                    Character.valueOf('p'),
                    "plankWood"));
        }
        boolean craftingRecipeChessDeskClear =
                config.getBoolean("craftingRecipeChessDeskClear", this.getName(), true,
                                  "clear chess game using crafting");
        if (craftingRecipeChessDeskClear) {
            ShapelessOreRecipe recipe =
                    new ShapelessOreRecipe(this.itemChessDesk, this.itemChessDesk);
            recipe.setRegistryName(DEF.MOD_ID, "chessDeskClear");
            ForgeRegistries.RECIPES.register(recipe);
        }
        
        Core.instance.network.registerMessage(MessageChessMove.class,
                                              MessageChessMove.class, 40,
                                              Side.SERVER);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.tileChessDeskRenderer = new TileEntityChessDeskRenderer();
        this.tileChessDeskRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        this.itemChessDeskRenderer = new ItemRendererChessDesk();
        this.itemChessDesk.setTileEntityItemStackRenderer(this.itemChessDeskRenderer);
        this.configChangedClient(config);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void configChangedClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        config.restartless();
        this.renderChessDeskItem = config.getBoolean("chessDeskItem", CLIENT_RENDER, true, "");
        boolean chessDeskTile = config.getBoolean("chessDeskTile", CLIENT_RENDER, true, "");
        if (chessDeskTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChessDesk.class,
                                                         this.tileChessDeskRenderer);
        } else {
            TileEntityRendererDispatcher.instance.renderers.remove(TileEntityChessDesk.class,
                                                                   this.tileChessDeskRenderer);
        }
        config.restartlessReset();
    }
    
    @Override
    public String getName() {
        return "deskgame";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public List<Item> getItems() {
        return Arrays.asList(this.itemChessDesk);
    }
}
