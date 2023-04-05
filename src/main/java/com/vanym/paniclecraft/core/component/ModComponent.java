package com.vanym.paniclecraft.core.component;

import java.util.List;

import com.vanym.paniclecraft.core.ModConfig;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ModComponent {
    
    public static final String ENABLE_FLAG = "enable";
    
    @SideOnly(Side.CLIENT)
    public static final String CLIENT_RENDER = "clientRender";
    
    public void preInit(ModConfig config);
    
    default public void init(ModConfig config) {}
    
    default public void configChanged(ModConfig config) {}
    
    @SideOnly(Side.CLIENT)
    default public void preInitClient(ModConfig config) {}
    
    @SideOnly(Side.CLIENT)
    default public void initClient(ModConfig config) {}
    
    @SideOnly(Side.CLIENT)
    default public void configChangedClient(ModConfig config) {}
    
    public String getName();
    
    public boolean isEnabled();
    
    default public List<Item> getItems() {
        return null;
    }
    
    default public void setServerSideConfig(IServerSideConfig config) {}
    
    default public IServerSideConfig getServerSideConfig() {
        return null;
    }
    
    public interface IServerSideConfig extends IMessage {
        public IServerSideConfig copy();
    }
}
