package ee_man.mod3.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ee_man.mod3.tileentity.TileEntityChessDesk;

public class MessageChessMove implements IMessage, IMessageHandler<MessageChessMove, IMessage>{
	
	int x;
	short y;
	int z;
	byte from;
	byte to;
	
	public MessageChessMove(){
	}
	
	public MessageChessMove(int parX, short parY, int parZ, byte parFrom, byte parTo){
		x = parX;
		y = parY;
		z = parZ;
		from = parFrom;
		to = parTo;
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		x = buf.readInt();
		y = buf.readShort();
		z = buf.readInt();
		from = buf.readByte();
		to = buf.readByte();
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(x);
		buf.writeShort(y);
		buf.writeInt(z);
		buf.writeByte(from);
		buf.writeByte(to);
	}
	
	@Override
	public IMessage onMessage(MessageChessMove message, MessageContext ctx){
		EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
		TileEntity tile = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if(tile instanceof TileEntityChessDesk && playerEntity.getDistanceSq(message.x + 0.5D, message.y + 0.5D, message.z + 0.5D) <= 64.0D){
			TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
			if(tileCD.desk.canGoTo(message.from, message.to) && tileCD.desk.needChoose() == 0 && (tileCD.desk.isWhiteTurn ? tileCD.desk.desk[message.from] > 0 && (tileCD.whitePlayer.equalsIgnoreCase(playerEntity.getGameProfile().getName()) || tileCD.whitePlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)) : tileCD.desk.desk[message.from] < 0 && (tileCD.blackPlayer.equalsIgnoreCase(playerEntity.getGameProfile().getName()) || tileCD.blackPlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)))){
				tileCD.desk.make(message.from, message.to);
			}
			tileCD.getWorldObj().markBlockForUpdate(tileCD.xCoord, tileCD.yCoord, tileCD.zCoord);
		}
		return null;
	}
}
