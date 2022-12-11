package ee_man.mod3.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ee_man.mod3.tileentity.TileEntityChessDesk;
import ee_man.mod3.utils.ChessDesk;

public class MessageChessNewGame implements IMessage, IMessageHandler<MessageChessNewGame, IMessage>{
	
	int x;
	short y;
	int z;
	String whitePlayer;
	String blackPlayer;
	
	public MessageChessNewGame(){
	}
	
	public MessageChessNewGame(int parX, short parY, int parZ, String parWhitePlayer, String parBlackPlayer){
		x = parX;
		y = parY;
		z = parZ;
		whitePlayer = parWhitePlayer;
		blackPlayer = parBlackPlayer;
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		x = buf.readInt();
		y = buf.readShort();
		z = buf.readInt();
		whitePlayer = ByteBufUtils.readUTF8String(buf);
		blackPlayer = ByteBufUtils.readUTF8String(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(x);
		buf.writeShort(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, whitePlayer);
		ByteBufUtils.writeUTF8String(buf, blackPlayer);
	}
	
	@Override
	public IMessage onMessage(MessageChessNewGame message, MessageContext ctx){
		EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
		TileEntity tile = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if(tile instanceof TileEntityChessDesk && playerEntity.getDistanceSq(message.x + 0.5D, message.y + 0.5D, message.z + 0.5D) <= 64.0D){
			TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
			tileCD.desk = new ChessDesk();
			tileCD.whitePlayer = message.whitePlayer;
			tileCD.blackPlayer = message.blackPlayer;
			tileCD.getWorldObj().markBlockForUpdate(tileCD.xCoord, tileCD.yCoord, tileCD.zCoord);
		}
		return null;
	}
}
