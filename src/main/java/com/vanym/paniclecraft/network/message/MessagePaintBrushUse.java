package com.vanym.paniclecraft.network.message;

import java.util.Objects;

import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPaintBrush;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessagePaintBrushUse
        implements
            IMessage,
            IMessageHandler<MessagePaintBrushUse, IMessage> {
    int x, y, z, px, py;
    byte side;
    
    public MessagePaintBrushUse() {}
    
    public MessagePaintBrushUse(int x, int y, int z, int px, int py, byte side) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.px = px;
        this.py = py;
        this.side = side;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z, this.px, this.py, this.side);
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MessagePaintBrushUse) && this.equals((MessagePaintBrushUse)obj);
    }
    
    public boolean equals(MessagePaintBrushUse mes) {
        return mes != null
            && this.x == mes.x
            && this.y == mes.y
            && this.z == mes.z
            && this.px == mes.px
            && this.py == mes.py
            && this.side == mes.side;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.px = buf.readInt();
        this.py = buf.readInt();
        this.side = buf.readByte();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.px);
        buf.writeInt(this.py);
        buf.writeByte(this.side);
    }
    
    @Override
    public IMessage onMessage(MessagePaintBrushUse message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        if (playerEntity.getHeldItem() != null) {
            if (playerEntity.getHeldItem().getItem() instanceof ItemPaintBrush) {
                TileEntity tileEntity =
                        playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
                if (tileEntity instanceof ISidePictureProvider
                    && playerEntity.canPlayerEdit(message.x, message.y, message.z, message.side,
                                                  playerEntity.getHeldItem())) {
                    Picture picture =
                            ((ISidePictureProvider)tileEntity).getPainting(message.side);
                    if (picture != null) {
                        picture.usePaintingTool(playerEntity.getHeldItem(), message.px, message.py);
                    }
                }
            }
        }
        
        return null;
    }
}
