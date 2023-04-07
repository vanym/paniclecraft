package com.vanym.paniclecraft.network.message;

import java.io.IOException;

import com.vanym.paniclecraft.container.ContainerPaintingViewServer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class MessagePaintingViewAddPicture implements IMessage {
    
    int x, y;
    ItemStack stack;
    
    public MessagePaintingViewAddPicture() {}
    
    public MessagePaintingViewAddPicture(int x, int y, ItemStack stack) {
        this.x = x;
        this.y = y;
        this.stack = stack;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.x = pb.readInt();
        this.y = pb.readInt();
        try {
            this.stack = pb.readItemStackFromBuffer();
        } catch (IOException e) {
            this.stack = null;
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeInt(this.x);
        pb.writeInt(this.y);
        try {
            pb.writeItemStackToBuffer(this.stack);
        } catch (IOException e) {
            pb.writeShort(-1);
        }
    }
    
    public static class Handler
            implements
                IMessageHandler<MessagePaintingViewAddPicture, IMessage> {
        
        @Override
        public IMessage onMessage(MessagePaintingViewAddPicture message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            if (!(playerEntity.openContainer instanceof ContainerPaintingViewServer)) {
                return null;
            }
            ContainerPaintingViewServer view =
                    (ContainerPaintingViewServer)playerEntity.openContainer;
            if (!view.isEditable()) {
                return null;
            }
            Picture picture = new Picture(true);
            if (message.stack == null || !ItemPainting.fillPicture(picture, message.stack)) {
                return null;
            }
            view.addPicture(message.x, message.y, picture);
            picture.unload();
            return null;
        }
    }
}
