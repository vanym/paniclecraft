package com.vanym.paniclecraft.network.message;

import java.util.NoSuchElementException;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.IModComponent;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageComponentConfig
        implements
            IMessage,
            IMessageHandler<MessageComponentConfig, IMessage> {
    
    IModComponent component;
    IModComponent.IServerSideConfig config;
    
    public MessageComponentConfig() {}
    
    public MessageComponentConfig(IModComponent component) {
        this.component = component;
        this.config = component.getServerSideConfig();
    }
    
    public boolean isEmpty() {
        return this.config == null;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        String name = ByteBufUtils.readUTF8String(buf);
        try {
            this.component = Core.instance.getComponents()
                                          .stream()
                                          .filter(component->name.equals(component.getName()))
                                          .findAny()
                                          .get();
        } catch (NoSuchElementException e) {
            return;
        }
        this.config = this.component.getServerSideConfig();
        if (this.config != null) {
            this.config = this.config.copy();
            this.config.fromBytes(buf);
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.component.getName());
        if (this.config != null) {
            this.config.toBytes(buf);
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageComponentConfig message, MessageContext ctx) {
        if (message.component != null && message.config != null) {
            message.component.setServerSideConfig(message.config);
        }
        return null;
    }
}
