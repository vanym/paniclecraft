package ee_man.mod3.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ee_man.mod3.item.ItemPaintBrush;
import ee_man.mod3.utils.ISidePaintingProvider;
import ee_man.mod3.utils.Painting;

public class MessagePaintBrushUse implements IMessage, IMessageHandler<MessagePaintBrushUse, IMessage>{
	int x, y, z, px, py;
	byte side;
	
	public MessagePaintBrushUse(){
	}
	
	public MessagePaintBrushUse(int par1x, int par2y, int par3z, int par4px, int par5py, byte par6side){
		this.x = par1x;
		this.y = par2y;
		this.z = par3z;
		this.px = par4px;
		this.py = par5py;
		this.side = par6side;
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.px = buf.readInt();
		this.py = buf.readInt();
		this.side = buf.readByte();
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(px);
		buf.writeInt(py);
		buf.writeByte(side);
	}
	
	@Override
	public IMessage onMessage(MessagePaintBrushUse message, MessageContext ctx){
		EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
		if(playerEntity.getHeldItem() != null)
			if(playerEntity.getHeldItem().getItem() instanceof ItemPaintBrush){
				TileEntity tileEntity = playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
				if(tileEntity instanceof ISidePaintingProvider && playerEntity.canPlayerEdit(message.x, message.y, message.z, message.side, playerEntity.getHeldItem())){
					Painting picture = ((ISidePaintingProvider)tileEntity).getPainting(message.side);
					if(picture != null)
						picture.usePaintBrush(playerEntity.getHeldItem(), message.px, message.py, true);
				}
			}
		
		return null;
	}
}
