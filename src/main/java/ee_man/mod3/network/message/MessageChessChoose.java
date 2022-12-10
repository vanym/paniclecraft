package ee_man.mod3.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ee_man.mod3.tileentity.TileEntityChessDesk;
import ee_man.mod3.utils.ChessDesk;

public class MessageChessChoose implements IMessage, IMessageHandler<MessageChessChoose, IMessage>{
	
	int x;
	short y;
	int z;
	byte choose;
	
	public MessageChessChoose(){
	}
	
	public MessageChessChoose(int parX, short parY, int parZ, byte parChoose){
		x = parX;
		y = parY;
		z = parZ;
		choose = parChoose;
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		x = buf.readInt();
		y = buf.readShort();
		z = buf.readInt();
		choose = buf.readByte();
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(x);
		buf.writeShort(y);
		buf.writeInt(z);
		buf.writeByte(choose);
	}
	
	@Override
	public IMessage onMessage(MessageChessChoose message, MessageContext ctx){
		EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
		TileEntity tile = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if(tile instanceof TileEntityChessDesk && playerEntity.getDistanceSq(message.x + 0.5D, message.y + 0.5D, message.z + 0.5D) <= 64.0D){
			TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
			if(tileCD.desk.needChoose() != 0 && message.choose > 1 && message.choose < 6 && (tileCD.desk.needChoose() > 0 ? (tileCD.whitePlayer.equalsIgnoreCase(playerEntity.getDisplayName()) || tileCD.whitePlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)) : (tileCD.blackPlayer.equalsIgnoreCase(playerEntity.getDisplayName()) || tileCD.blackPlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)))){
				int fig = tileCD.desk.needChoose();
				tileCD.desk.desk[ChessDesk.getFromXY(Math.abs(fig) - 1, (fig > 0 ? 7 : 0))] = (byte)(fig > 0 ? message.choose : -message.choose);
			}
			tileCD.getWorldObj().markBlockForUpdate(tileCD.xCoord, tileCD.yCoord, tileCD.zCoord);
		}
		return null;
	}
}
