package ee_man.mod3.network.message;

import io.netty.buffer.ByteBuf;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ee_man.mod3.container.ContainerPalette;
import ee_man.mod3.utils.MainUtils;

public class MessagePaletteChange implements IMessage, IMessageHandler<MessagePaletteChange, IMessage>{
	
	byte bt;
	
	public MessagePaletteChange(){
	}
	
	public MessagePaletteChange(byte parBt){
		bt = parBt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		bt = buf.readByte();
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeByte(bt);
	}
	
	@Override
	public IMessage onMessage(MessagePaletteChange message, MessageContext ctx){
		EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
		if(playerEntity.openContainer instanceof ContainerPalette){
			ContainerPalette palette = (ContainerPalette)playerEntity.openContainer;
			Color color = MainUtils.getColorFromInt(palette.inventoryPalette.getRGB());
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
			switch(message.bt){
				case 0:
					red--;
				break;
				case 1:
					red++;
				break;
				case 2:
					green--;
				break;
				case 3:
					green++;
				break;
				case 4:
					blue--;
				break;
				case 5:
					blue++;
				break;
				case 0 + 6:
					red -= 10;
				break;
				case 1 + 6:
					red += 10;
				break;
				case 2 + 6:
					green -= 10;
				break;
				case 3 + 6:
					green += 10;
				break;
				case 4 + 6:
					blue -= 10;
				break;
				case 5 + 6:
					blue += 10;
				break;
				case 0 + 12:
					red -= 50;
				break;
				case 1 + 12:
					red += 50;
				break;
				case 2 + 12:
					green -= 50;
				break;
				case 3 + 12:
					green += 50;
				break;
				case 4 + 12:
					blue -= 50;
				break;
				case 5 + 12:
					blue += 50;
				break;
			}
			while (red < 0)
				red += 256;
			while (red >= 256)
				red -= 256;
			while (green < 0)
				green += 256;
			while (green >= 256)
				green -= 256;
			while (blue < 0)
				blue += 256;
			while (blue >= 256)
				blue -= 256;
			palette.inventoryPalette.setColor(red, green, blue);
		}
		return null;
	}
}
