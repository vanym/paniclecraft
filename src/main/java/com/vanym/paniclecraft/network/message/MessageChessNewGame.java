package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.ChessDesk;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageChessNewGame
        implements
            IMessage,
            IMessageHandler<MessageChessNewGame, IMessage> {
    
    int x;
    short y;
    int z;
    String whitePlayer;
    String blackPlayer;
    
    public MessageChessNewGame() {
    }
    
    public MessageChessNewGame(int parX,
            short parY,
            int parZ,
            String parWhitePlayer,
            String parBlackPlayer) {
        this.x = parX;
        this.y = parY;
        this.z = parZ;
        this.whitePlayer = parWhitePlayer;
        this.blackPlayer = parBlackPlayer;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readShort();
        this.z = buf.readInt();
        this.whitePlayer = ByteBufUtils.readUTF8String(buf);
        this.blackPlayer = ByteBufUtils.readUTF8String(buf);
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeShort(this.y);
        buf.writeInt(this.z);
        ByteBufUtils.writeUTF8String(buf, this.whitePlayer);
        ByteBufUtils.writeUTF8String(buf, this.blackPlayer);
    }
    
    @Override
    public IMessage onMessage(MessageChessNewGame message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        TileEntity tile = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if (tile instanceof TileEntityChessDesk
            && playerEntity.getDistanceSq(message.x + 0.5D, message.y + 0.5D,
                                          message.z + 0.5D) <= 64.0D) {
            TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
            tileCD.desk = new ChessDesk();
            tileCD.whitePlayer = message.whitePlayer;
            tileCD.blackPlayer = message.blackPlayer;
            tileCD.getWorldObj().markBlockForUpdate(tileCD.xCoord, tileCD.yCoord, tileCD.zCoord);
        }
        return null;
    }
}
