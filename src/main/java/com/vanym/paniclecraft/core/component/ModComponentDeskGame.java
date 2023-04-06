package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockChessDesk;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererChessDesk;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityChessDeskRenderer;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModComponentDeskGame implements IModComponent {
    
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
        GameRegistry.registerBlock(this.blockChessDesk, ItemChessDesk.class,
                                   this.blockChessDesk.getName());
        this.itemChessDesk = (ItemChessDesk)Item.getItemFromBlock(this.blockChessDesk);
        GameRegistry.registerTileEntity(TileEntityChessDesk.class,
                                        TileEntityChessDesk.ID.toString());
        boolean craftingRecipeChessDesk =
                config.getBoolean("craftingRecipeChessDesk", this.getName(), true, "");
        if (craftingRecipeChessDesk) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    this.itemChessDesk,
                    true,
                    "w b",
                    "ppp",
                    Character.valueOf('w'),
                    new ItemStack(Blocks.wool, 1, 0),
                    Character.valueOf('b'),
                    new ItemStack(Blocks.wool, 1, 15),
                    Character.valueOf('p'),
                    "plankWood"));
        }
        boolean craftingRecipeChessDeskClear =
                config.getBoolean("craftingRecipeChessDeskClear", this.getName(), true,
                                  "clear chess game using crafting");
        if (craftingRecipeChessDeskClear) {
            GameRegistry.addShapelessRecipe(new ItemStack(this.itemChessDesk, 1),
                                            this.itemChessDesk);
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
        this.tileChessDeskRenderer.func_147497_a(TileEntityRendererDispatcher.instance);
        this.itemChessDeskRenderer = new ItemRendererChessDesk();
        MinecraftForgeClient.registerItemRenderer(this.itemChessDesk, this.itemChessDeskRenderer);
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
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(TileEntityChessDesk.class,
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
