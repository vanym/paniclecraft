package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public class MessageAdvSignChange
        implements
            IMessage,
            IMessageHandler<MessageAdvSignChange, IMessage> {
    int x, y, z;
    byte red, green, blue;
    String text;
    
    public MessageAdvSignChange() {
    }
    
    public MessageAdvSignChange(int par1x,
            int par2y,
            int par3z,
            byte par4red,
            byte par5green,
            byte par6blue,
            String par7text) {
        this.x = par1x;
        this.y = par2y;
        this.z = par3z;
        this.red = par4red;
        this.green = par5green;
        this.blue = par6blue;
        this.text = par7text;
    }
    
    public MessageAdvSignChange(TileEntityAdvSign entitySign) {
        this.x = entitySign.xCoord;
        this.y = entitySign.yCoord;
        this.z = entitySign.zCoord;
        this.red = entitySign.red;
        this.green = entitySign.green;
        this.blue = entitySign.blue;
        this.text = entitySign.signText;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.red = buf.readByte();
        this.green = buf.readByte();
        this.blue = buf.readByte();
        this.text = ByteBufUtils.readUTF8String(buf);
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.red);
        buf.writeByte(this.green);
        buf.writeByte(this.blue);
        ByteBufUtils.writeUTF8String(buf, this.text);
    }
    
    @Override
    public IMessage onMessage(MessageAdvSignChange message, MessageContext ctx) {
        TileEntity tile =
                ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y,
                                                                           message.z);
        if (tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileA = (TileEntityAdvSign)tile;
            if (tileA.isEditable) {
                tileA.red = message.red;
                tileA.green = message.green;
                tileA.blue = message.blue;
                tileA.signText = message.text;
                tileA.markDirty();
                tileA.getWorldObj().markBlockForUpdate(tileA.xCoord, tileA.yCoord, tileA.zCoord);
            }
        }
        return null;
    }
}
