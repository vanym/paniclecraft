package com.vanym.paniclecraft.core.component;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockCannon;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererCannon;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityCannonRenderer;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemCannon;
import com.vanym.paniclecraft.network.message.MessageCannonChange;
import com.vanym.paniclecraft.network.message.MessageCannonSet;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModComponentCannon implements ModComponent {
    
    public BlockCannon blockCannon;
    public ItemCannon itemCannon;
    
    @SideOnly(Side.CLIENT)
    public TileEntityCannonRenderer tileCannonRenderer = new TileEntityCannonRenderer();
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        this.blockCannon = new BlockCannon();
        this.itemCannon = new ItemCannon();
        GameRegistry.registerBlock(this.blockCannon, null, this.blockCannon.getName());
        Core.instance.registerItem(this.itemCannon);
        GameRegistry.registerTileEntity(TileEntityCannon.class, DEF.MOD_ID + ".cannon");
        boolean craftingRecipeCannon =
                config.getBoolean("craftingRecipeCannon", this.getName(), true, "");
        if (craftingRecipeCannon) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    this.itemCannon,
                    "i  ",
                    " i ",
                    "idi",
                    Character.valueOf('i'),
                    "ingotIron",
                    Character.valueOf('d'),
                    Blocks.dispenser));
        }
        
        Core.instance.network.registerMessage(MessageCannonChange.class,
                                              MessageCannonChange.class, 50,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessageCannonSet.class, MessageCannonSet.class, 51,
                                              Side.SERVER);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        boolean cannonTile = config.getBoolean("cannonTile", CLIENT_RENDER, true, "");
        if (cannonTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class,
                                                         this.tileCannonRenderer);
        }
        boolean cannonItem = config.getBoolean("cannonItem", CLIENT_RENDER, true, "");
        if (cannonItem) {
            MinecraftForgeClient.registerItemRenderer(this.itemCannon, new ItemRendererCannon());
        }
    }
    
    @Override
    public String getName() {
        return "cannon";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
