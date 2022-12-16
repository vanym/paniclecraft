package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageChessMove implements IMessage, IMessageHandler<MessageChessMove, IMessage> {
    
    int x;
    short y;
    int z;
    byte from;
    byte to;
    
    public MessageChessMove() {}
    
    public MessageChessMove(int parX, short parY, int parZ, byte parFrom, byte parTo) {
        this.x = parX;
        this.y = parY;
        this.z = parZ;
        this.from = parFrom;
        this.to = parTo;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readShort();
        this.z = buf.readInt();
        this.from = buf.readByte();
        this.to = buf.readByte();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeShort(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.from);
        buf.writeByte(this.to);
    }
    
    @Override
    public IMessage onMessage(MessageChessMove message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        TileEntity tile = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if (tile instanceof TileEntityChessDesk
            && playerEntity.getDistanceSq(message.x + 0.5D, message.y + 0.5D,
                                          message.z + 0.5D) <= 64.0D) {
            TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
            if (tileCD.desk.canGoTo(message.from, message.to) && tileCD.desk.needChoose() == 0
                && (tileCD.desk.isWhiteTurn ? tileCD.desk.desk[message.from] > 0
                    && (tileCD.whitePlayer.equalsIgnoreCase(playerEntity.getGameProfile().getName())
                        || tileCD.whitePlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer))
                                            : tileCD.desk.desk[message.from] < 0
                                                && (tileCD.blackPlayer.equalsIgnoreCase(playerEntity.getGameProfile()
                                                                                                    .getName())
                                                    || tileCD.blackPlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)))) {
                tileCD.desk.make(message.from, message.to);
            }
            tileCD.getWorldObj().markBlockForUpdate(tileCD.xCoord, tileCD.yCoord, tileCD.zCoord);
        }
        return null;
    }
}
