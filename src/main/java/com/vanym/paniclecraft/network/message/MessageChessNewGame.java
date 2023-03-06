package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

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
    int y;
    int z;
    
    public MessageChessNewGame() {}
    
    public MessageChessNewGame(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }
    
    @Override
    public IMessage onMessage(MessageChessNewGame message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        TileEntity tile = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if (tile instanceof TileEntityChessDesk
            && playerEntity.getDistanceSq(message.x + 0.5D, message.y + 0.5D,
                                          message.z + 0.5D) <= 64.0D) {
            TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
            tileCD.resetGame();
            tileCD.markForUpdate();
        }
        return null;
    }
}
