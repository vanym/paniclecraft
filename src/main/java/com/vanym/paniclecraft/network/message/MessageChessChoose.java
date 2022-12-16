package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.ChessDesk;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageChessChoose implements IMessage, IMessageHandler<MessageChessChoose, IMessage> {
    
    int x;
    short y;
    int z;
    byte choose;
    
    public MessageChessChoose() {}
    
    public MessageChessChoose(int parX, short parY, int parZ, byte parChoose) {
        this.x = parX;
        this.y = parY;
        this.z = parZ;
        this.choose = parChoose;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readShort();
        this.z = buf.readInt();
        this.choose = buf.readByte();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeShort(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.choose);
    }
    
    @Override
    public IMessage onMessage(MessageChessChoose message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        TileEntity tile = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if (tile instanceof TileEntityChessDesk
            && playerEntity.getDistanceSq(message.x + 0.5D, message.y + 0.5D,
                                          message.z + 0.5D) <= 64.0D) {
            TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
            if (tileCD.desk.needChoose() != 0 && message.choose > 1
                && message.choose < 6
                && (tileCD.desk.needChoose() > 0 ? (tileCD.whitePlayer.equalsIgnoreCase(playerEntity.getDisplayName())
                    || tileCD.whitePlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer))
                                                 : (tileCD.blackPlayer.equalsIgnoreCase(playerEntity.getDisplayName())
                                                     || tileCD.blackPlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)))) {
                int fig = tileCD.desk.needChoose();
                tileCD.desk.desk[ChessDesk.getFromXY(Math.abs(fig) - 1, (fig > 0 ? 7 : 0))] =
                        (byte)(fig > 0 ? message.choose : -message.choose);
            }
            tileCD.getWorldObj().markBlockForUpdate(tileCD.xCoord, tileCD.yCoord, tileCD.zCoord);
        }
        return null;
    }
}
