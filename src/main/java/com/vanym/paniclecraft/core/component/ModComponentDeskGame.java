package com.vanym.paniclecraft.core.component;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockChessDesk;
import com.vanym.paniclecraft.client.ModConfig;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererChessDesk;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityChessDeskRenderer;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.network.message.MessageChessChoose;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.network.message.MessageChessNewGame;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModComponentDeskGame implements ModComponent {
    
    public BlockChessDesk blockChessDesk;
    public ItemChessDesk itemChessDesk;
    
    @SideOnly(Side.CLIENT)
    public TileEntityChessDeskRenderer tileChessDeskRenderer = new TileEntityChessDeskRenderer();
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        this.itemChessDesk = new ItemChessDesk();
        this.blockChessDesk = new BlockChessDesk();
        Core.instance.registerItem(this.itemChessDesk);
        GameRegistry.registerBlock(this.blockChessDesk, null, this.blockChessDesk.getName());
        GameRegistry.registerTileEntity(TileEntityChessDesk.class, DEF.MOD_ID + ".chessDesk");
        boolean craftingRecipeChessDesk =
                config.getBoolean("craftingRecipeChessDesk", this.getName(), true, "");
        if (craftingRecipeChessDesk) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    this.itemChessDesk,
                    "w b",
                    "ppp",
                    Character.valueOf('w'),
                    new ItemStack(Blocks.wool, 1, 0),
                    Character.valueOf('b'),
                    new ItemStack(Blocks.wool, 1, 15),
                    Character.valueOf('p'),
                    "plankWood"));
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    this.itemChessDesk,
                    "b w",
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
        Core.instance.network.registerMessage(MessageChessChoose.class,
                                              MessageChessChoose.class, 41,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessageChessNewGame.class,
                                              MessageChessNewGame.class, 42,
                                              Side.SERVER);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        boolean chessDeskTile = config.getBoolean("chessDeskTile", CLIENT_RENDER, true, "");
        if (chessDeskTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChessDesk.class,
                                                         this.tileChessDeskRenderer);
        }
        boolean chessDeskItem = config.getBoolean("chessDeskItem", CLIENT_RENDER, true, "");
        if (chessDeskItem) {
            MinecraftForgeClient.registerItemRenderer(this.itemChessDesk,
                                                      new ItemRendererChessDesk());
        }
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
