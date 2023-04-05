package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChessMove implements IMessage, IMessageHandler<MessageChessMove, IMessage> {
    
    int x;
    int y;
    int z;
    ChessGame.Move move;
    
    public MessageChessMove() {}
    
    public MessageChessMove(int x, int y, int z, ChessGame.Move move) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.move = move;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        try {
            this.move = new ChessGame.Move(ByteBufUtils.readUTF8String(buf));
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        ByteBufUtils.writeUTF8String(buf, this.move.toString(false));
    }
    
    @Override
    public IMessage onMessage(MessageChessMove message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().player;
        BlockPos pos = new BlockPos(message.x, message.y, message.z);
        TileEntity tile = playerEntity.world.getTileEntity(pos);
        if (message.move != null && tile instanceof TileEntityChessDesk
            && playerEntity.getDistanceSq(message.x + 0.5D, message.y + 0.5D,
                                          message.z + 0.5D) <= 64.0D) {
            TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
            tileCD.move(message.move, playerEntity);
        }
        return null;
    }
}
