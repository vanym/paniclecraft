package com.vanym.paniclecraft.core.component;

import java.util.ArrayList;
import java.util.List;

import com.vanym.paniclecraft.Core;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModComponentDeskGame extends ModComponent {
    
    @ModComponentObject
    public BlockChessDesk blockChessDesk;
    @ModComponentObject
    public ItemChessDesk itemChessDesk;
    
    protected List<IRecipe> recipes = new ArrayList<>();
    
    @SideOnly(Side.CLIENT)
    public TileEntityChessDeskRenderer tileChessDeskRenderer;
    @SideOnly(Side.CLIENT)
    public ItemRendererChessDesk itemChessDeskRenderer;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        MinecraftForge.EVENT_BUS.register(this);
        this.blockChessDesk = new BlockChessDesk();
        this.itemChessDesk = new ItemChessDesk(this.blockChessDesk);
        GameRegistry.registerTileEntity(TileEntityChessDesk.class, TileEntityChessDesk.ID);
        boolean craftingRecipeChessDesk =
                config.getBoolean("craftingRecipeChessDesk", this.getName(), true, "");
        if (craftingRecipeChessDesk) {
            this.recipes.add(new ShapedOreRecipe(
                    this.itemChessDesk,
                    true,
                    "w b",
                    "ppp",
                    Character.valueOf('w'),
                    "woolWhite",
                    Character.valueOf('b'),
                    "woolBlack",
                    Character.valueOf('p'),
                    "plankWood").flow());
        }
        boolean craftingRecipeChessDeskClear =
                config.getBoolean("craftingRecipeChessDeskClear", this.getName(), true,
                                  "clear chess game using crafting");
        if (craftingRecipeChessDeskClear) {
            ShapelessOreRecipe recipe = new ShapelessOreRecipe(
                    this.itemChessDesk,
                    new ItemStack(this.itemChessDesk));
            RecipeRegister.flowRegistryName(recipe, "%s_clear");
            this.recipes.add(recipe);
        }
        
        Core.instance.network.registerMessage(MessageChessMove.Handler.class,
                                              MessageChessMove.class, 40,
                                              Side.SERVER);
    }
    
    @Override
    public List<IRecipe> getRecipes() {
        return this.recipes;
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
}
