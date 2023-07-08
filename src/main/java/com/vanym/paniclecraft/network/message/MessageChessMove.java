package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageChessMove {
    
    protected final BlockPos pos;
    protected final ChessGame.Move move;
    
    public MessageChessMove(BlockPos pos, ChessGame.Move move) {
        this.pos = pos;
        this.move = move;
    }
    
    public static void encode(MessageChessMove message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
        buf.writeString(message.move.toString(false));
    }
    
    public static MessageChessMove decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        ChessGame.Move move = new ChessGame.Move(buf.readString());
        return new MessageChessMove(pos, move);
    }
    
    public static void handleInWorld(MessageChessMove message, NetworkEvent.Context ctx) {
        PlayerEntity player = ctx.getSender();
        TileEntity tile = player.world.getTileEntity(message.pos);
        if (message.move != null && tile instanceof TileEntityChessDesk
            && player.getDistanceSq(new Vec3d(tile.getPos()).add(0.5D, 0.5D, 0.5D)) <= 64.0D) {
            TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
            tileCD.move(message.move, player);
        }
    }
}
