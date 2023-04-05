package com.vanym.paniclecraft.network.message;

import java.awt.Color;

import com.vanym.paniclecraft.container.ContainerPalette;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePaletteSetColor
        implements
            IMessage,
            IMessageHandler<MessagePaletteSetColor, IMessage> {
    
    protected Color color;
    
    public MessagePaletteSetColor() {}
    
    public MessagePaletteSetColor(Color color) {
        this.color = color;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.color = new Color(buf.readInt(), true);
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.color.getRGB());
    }
    
    @Override
    public IMessage onMessage(MessagePaletteSetColor message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().player;
        if (!(playerEntity.openContainer instanceof ContainerPalette)) {
            return null;
        }
        ContainerPalette palette = (ContainerPalette)playerEntity.openContainer;
        palette.setColor(message.color);
        return null;
    }
}
