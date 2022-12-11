package ee_man.mod3.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.utils.ISidePaintingProvider;
import ee_man.mod3.utils.Painting;

public class TileEntityPainting extends TileEntity implements ISidePaintingProvider{
	
	private boolean need_to_update = false;
	
	private Painting picture;
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		NBTTagCompound paintingTag = new NBTTagCompound();
		this.getPicture().writeToNBT(paintingTag);
		par1NBTTagCompound.setTag("PictureData", paintingTag);
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		if(par1NBTTagCompound.hasKey("PictureData"))
			this.getPicture().readFromNBT(par1NBTTagCompound.getCompoundTag("PictureData"));
		else
			picture = null;
	}
	
	public Packet getDescriptionPacket(){
		NBTTagCompound dataTag = new NBTTagCompound();
		this.writeToNBT(dataTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, dataTag);
	}
	
	public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet){
		NBTTagCompound nbtData = packet.func_148857_g();
		this.readFromNBT(nbtData);
	}
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared(){
		return 16384.0D;
	}
	
	private Painting getPicture(){
		if(picture == null){
			picture = new Painting(this);
		}
		return picture;
	}
	
	@Override
	public Painting getPainting(int side){
		if(side == this.getBlockMetadata())
			return this.getPicture();
		else
			return null;
	}
	
	@Override
	public Painting getPainting(int side, int xO, int yO){
		int x = this.xCoord;
		int y = this.yCoord;
		int z = this.zCoord;
		switch(side){
			case 0:
				x -= xO;
				z += yO;
			break;
			case 1:
				x -= xO;
				z -= yO;
			break;
			case 2:
				x -= xO;
				y -= yO;
			break;
			case 3:
				x += xO;
				y -= yO;
			break;
			case 4:
				z += xO;
				y -= yO;
			break;
			case 5:
				z -= xO;
				y -= yO;
			break;
		}
		TileEntity var1 = this.getWorldObj().getTileEntity(x, y, z);
		if(var1 instanceof TileEntityPainting && ((TileEntityPainting)var1).getPicture().getRow() == this.getPicture().getRow() && var1.getBlockMetadata() == this.getBlockMetadata()){
			return ((TileEntityPainting)var1).getPainting(side);
		}
		return null;
	}
	
	public void needUpdate(){
		need_to_update = true;
	}
	
	public void updateEntity(){
		super.updateEntity();
		if(need_to_update)
			markForUpdate();
	}
	
	@Override
	public void markForUpdate(){
		this.getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
		need_to_update = false;
	}
	
	@Override
	public int getPictureSide(Painting picture){
		if(this.getPicture().equals(picture))
			return this.getBlockMetadata();
		else
			return -1;
	}
	
	@SideOnly(Side.CLIENT)
	public void invalidate(){
		super.invalidate();
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			this.getPicture().deleteTexrure();
	}
	
	@SideOnly(Side.CLIENT)
	public void onChunkUnload(){
		super.onChunkUnload();
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			this.getPicture().deleteTexrure();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onWorldUnload(){
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			this.getPicture().deleteTexrure();
	}
}
