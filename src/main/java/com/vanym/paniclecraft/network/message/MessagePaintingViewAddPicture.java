package com.vanym.paniclecraft.network.message;

import java.io.IOException;

import com.vanym.paniclecraft.container.ContainerPaintingViewServer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
            this.stack = pb.readItemStack();
        } catch (IOException e) {
            this.stack = ItemStack.EMPTY;
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeInt(this.x);
        pb.writeInt(this.y);
        pb.writeItemStack(this.stack);
    }
    
    public static class Handler
            implements
                IMessageHandler<MessagePaintingViewAddPicture, IMessage> {
        
        @Override
        public IMessage onMessage(MessagePaintingViewAddPicture message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            if (!(playerEntity.openContainer instanceof ContainerPaintingViewServer)) {
                return null;
            }
            ContainerPaintingViewServer view =
                    (ContainerPaintingViewServer)playerEntity.openContainer;
            if (!view.isEditable()) {
                return null;
            }
            Picture picture = new Picture(true);
            if (message.stack == null || message.stack.isEmpty()
                || !ItemPainting.fillPicture(picture, message.stack)) {
                return null;
            }
            view.addPicture(message.x, message.y, picture);
            picture.unload();
            return null;
        }
    }
}
