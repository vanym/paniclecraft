package ee_man.mod3.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ee_man.mod3.container.ContainerCannon;
import ee_man.mod3.tileentity.TileEntityCannon;

public class MessageCannonSet implements IMessage, IMessageHandler<MessageCannonSet, IMessage>{
	
	byte bt;
	double to;
	
	public MessageCannonSet(){
	}
	
	public MessageCannonSet(byte parBt, double parTo){
		bt = parBt;
		to = parTo;
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		bt = buf.readByte();
		to = buf.readDouble();
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeByte(bt);
		buf.writeDouble(to);
	}
	
	@Override
	public IMessage onMessage(MessageCannonSet message, MessageContext ctx){
		EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
		if(playerEntity.openContainer instanceof ContainerCannon){
			ContainerCannon containerCannon = (ContainerCannon)playerEntity.openContainer;
			TileEntityCannon tileCannon = containerCannon.tileCannon;
			switch(message.bt){
				case 0:{
					while(message.to >= 360)
						message.to -= 360;
					
					while(message.to < 0)
						message.to += 360;
					tileCannon.setDirection(message.to);
				}
				break;
				case 1:{
					if(message.to <= 90 && message.to >= -90)
						tileCannon.setHeight(message.to);
				}
				break;
				case 2:{
					if(message.to >= 0 && message.to <= tileCannon.maxStrength)
						tileCannon.setStrength(message.to);
				}
				break;
			}
			tileCannon.getWorldObj().markBlockForUpdate(tileCannon.xCoord, tileCannon.yCoord, tileCannon.zCoord);
		}
		return null;
	}
}
