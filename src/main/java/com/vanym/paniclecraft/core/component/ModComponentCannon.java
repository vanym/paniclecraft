package com.vanym.paniclecraft.core.component;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockCannon;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererCannon;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityCannonRenderer;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.item.ItemBlockMod3;
import com.vanym.paniclecraft.network.message.MessageCannonSet;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapedOreRecipe;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModComponentCannon extends ModComponent {
    
    @ModComponentObject
    public BlockCannon blockCannon;
    @ModComponentObject
    public ItemBlock itemCannon;
    
    @ModComponentObject
    protected IRecipe recipeCannon;
    
    protected ChangeableConfig myServerConfig = new ChangeableConfig();
    public ChangeableConfig config = this.myServerConfig;
    
    @SideOnly(Side.CLIENT)
    public TileEntityCannonRenderer tileCannonRenderer;
    @SideOnly(Side.CLIENT)
    public ItemRendererCannon itemCannonRenderer;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        MinecraftForge.EVENT_BUS.register(this);
        this.blockCannon = new BlockCannon();
        this.itemCannon = new ItemBlockMod3(this.blockCannon);
        this.itemCannon.setRegistryName(this.blockCannon.getRegistryName());
        GameRegistry.registerTileEntity(TileEntityCannon.class, TileEntityCannon.ID);
        boolean craftingRecipeCannon =
                config.getBoolean("craftingRecipeCannon", this.getName(), true, "");
        if (craftingRecipeCannon) {
            this.recipeCannon = new ShapedOreRecipe(
                    this.itemCannon,
                    "i  ",
                    " i ",
                    "idi",
                    Character.valueOf('i'),
                    "ingotIron",
                    Character.valueOf('d'),
                    Blocks.DISPENSER).flow();
        }
        
        Core.instance.network.registerMessage(MessageCannonSet.Handler.class,
                                              MessageCannonSet.class, 51, Side.SERVER);
        
        this.configChanged(config);
    }
    
    @Override
    public void configChanged(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.myServerConfig.read(config);
    }
    
    @Override
    public void init(ModConfig config) {
        CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
        TileEntityCannon.registerFixesCannon(fixer);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.tileCannonRenderer = new TileEntityCannonRenderer();
        this.tileCannonRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        this.itemCannonRenderer = new ItemRendererCannon();
        this.itemCannon.setTileEntityItemStackRenderer(this.itemCannonRenderer);
        MinecraftForge.EVENT_BUS.register(this.itemCannonRenderer);
        this.configChangedClient(config);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void configChangedClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        config.restartless();
        boolean cannonTile = config.getBoolean("cannonTile", CLIENT_RENDER, true, "");
        if (cannonTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class,
                                                         this.tileCannonRenderer);
        } else {
            TileEntityRendererDispatcher.instance.renderers.remove(TileEntityCannon.class,
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
    public void setServerSideConfig(IServerSideConfig config) {
        this.config = (ChangeableConfig)config;
    }
    
    @Override
    public IServerSideConfig getServerSideConfig() {
        return this.myServerConfig;
    }
    
    public class ChangeableConfig implements IServerSideConfig {
        
        public double maxStrength = 5.0D;
        public int pickupDelay = 25;
        public int shootTimeout = 2;
        
        protected ChangeableConfig() {}
        
        protected ChangeableConfig(ChangeableConfig config) {
            this.maxStrength = config.maxStrength;
            this.pickupDelay = config.pickupDelay;
            this.shootTimeout = config.shootTimeout;
        }
        
        public ChangeableConfig read(ModConfig config) {
            config.restartless();
            final String category = ModComponentCannon.this.getName();
            this.maxStrength = config.getDouble("maxStrength", category, 5.0D, 0.0D, 16.0D, "");
            this.shootTimeout = config.getInt("shootTimeout", category, 2, 0, Short.MAX_VALUE,
                                              "shoot timeout in game ticks");
            this.pickupDelay = config.getInt("pickupDelay", category, 25, 0, Short.MAX_VALUE,
                                             "shooted items pickup delay in game ticks");
            config.restartlessReset();
            return this;
        }
        
        @Override
        public void fromBytes(ByteBuf buf) {
            this.maxStrength = buf.readDouble();
            this.pickupDelay = buf.readInt();
            this.shootTimeout = buf.readInt();
        }
        
        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeDouble(this.maxStrength);
            buf.writeInt(this.pickupDelay);
            buf.writeInt(this.shootTimeout);
        }
        
        @Override
        public IServerSideConfig copy() {
            return new ChangeableConfig(this);
        }
    }
}
