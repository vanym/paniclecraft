package com.vanym.paniclecraft.core.component;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockCannon;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererCannon;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityCannonRenderer;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.network.message.MessageCannonSet;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModComponentCannon implements ModComponent {
    
    public BlockCannon blockCannon;
    public ItemBlock itemCannon;
    
    public ChangeableConfig config = new ChangeableConfig();
    
    @SideOnly(Side.CLIENT)
    public TileEntityCannonRenderer tileCannonRenderer;
    @SideOnly(Side.CLIENT)
    public ItemRendererCannon itemCannonRenderer;
    
    @SideOnly(Side.CLIENT)
    public boolean renderCannonItem = true;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        this.blockCannon = new BlockCannon();
        GameRegistry.registerBlock(this.blockCannon, this.blockCannon.getName());
        this.itemCannon = (ItemBlock)Item.getItemFromBlock(this.blockCannon);
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
        
        Core.instance.network.registerMessage(MessageCannonSet.class, MessageCannonSet.class, 51,
                                              Side.SERVER);
        
        this.config = new ChangeableConfig().read(config);
    }
    
    @Override
    public void configChanged(ModConfig config) {
        this.config.read(config);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.tileCannonRenderer = new TileEntityCannonRenderer();
        this.itemCannonRenderer = new ItemRendererCannon();
        MinecraftForgeClient.registerItemRenderer(this.itemCannon, this.itemCannonRenderer);
        boolean cannonTile = config.getBoolean("cannonTile", CLIENT_RENDER, true, "");
        if (cannonTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class,
                                                         this.tileCannonRenderer);
        }
        this.configChangedClient(config);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void configChangedClient(ModConfig config) {
        config.restartless();
        this.renderCannonItem = config.getBoolean("cannonItem", CLIENT_RENDER, true, "");
        boolean cannonTile = config.getBoolean("cannonTile", CLIENT_RENDER, true, "");
        if (cannonTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class,
                                                         this.tileCannonRenderer);
        } else {
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(TileEntityCannon.class,
                                                                             this.tileCannonRenderer);
        }
        config.restartlessReset();
    }
    
    @Override
    public String getName() {
        return "cannon";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public List<Item> getItems() {
        return Arrays.asList(this.itemCannon);
    }
    
    public class ChangeableConfig {
        
        public double maxStrength = 5.0D;
        
        protected ChangeableConfig() {}
        
        public ChangeableConfig read(ModConfig config) {
            config.restartless();
            this.maxStrength = config.getDouble("maxStrength", ModComponentCannon.this.getName(),
                                                5.0D, 0.0D, 16.0D, "");
            config.restartlessReset();
            return this;
        }
    }
}
