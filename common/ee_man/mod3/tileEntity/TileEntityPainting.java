package ee_man.mod3.tileEntity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.blocks.BlockPainting;
import ee_man.mod3.items.ItemPaintBrush;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityPainting extends TileEntity{
	
	public int Row = ItemPaintBrush.paintRow;
	public int BrushRadius = ItemPaintBrush.brushRadius;
	
	public int[][] NoDrawPixels = ItemPaintBrush.noDrawPixels.clone();
	public byte[] pic = new byte[Row * Row * 3];
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("Row", Row);
		par1NBTTagCompound.setByteArray("pic[" + Row + "]", pic);
		par1NBTTagCompound.setIntArray("NoDrawPixels[0]", NoDrawPixels[0]);
		par1NBTTagCompound.setIntArray("NoDrawPixels[1]", NoDrawPixels[1]);
		par1NBTTagCompound.setInteger("BrushRadius", BrushRadius);
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		Row = par1NBTTagCompound.getInteger("Row");
		pic = par1NBTTagCompound.getByteArray("pic[" + Row + "]");
		if(pic == null || pic.length == 0){
			pic = new byte[Row * Row * 3];
			for(int i = 0; i < pic.length; i++){
				pic[i] = (byte)ItemPaintBrush.DEFAULT_COLOR_RGB;
			}
		}
		if(par1NBTTagCompound.hasKey("NoDrawPixels[0]"))
		NoDrawPixels[0] = par1NBTTagCompound.getIntArray("NoDrawPixels[0]");
		if(par1NBTTagCompound.hasKey("NoDrawPixels[1]"))
		NoDrawPixels[1] = par1NBTTagCompound.getIntArray("NoDrawPixels[1]");
		if(par1NBTTagCompound.hasKey("BrushRadius"))
		BrushRadius = par1NBTTagCompound.getInteger("BrushRadius");
	}
	
	public Packet getDescriptionPacket(){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try{
			data.writeByte(2);
			data.writeInt(this.xCoord);
			data.writeInt(this.yCoord);
			data.writeInt(this.zCoord);
			data.writeInt(Row);
			data.writeInt(this.BrushRadius);
			data.writeInt(this.NoDrawPixels[0].length);
			for(int i = 0; i < this.NoDrawPixels[0].length; i++){
				data.writeInt(this.NoDrawPixels[0][i]);
			}
			for(int i = 0; i < this.NoDrawPixels[1].length; i++){
				data.writeInt(this.NoDrawPixels[1][i]);
			}
			for(int i = 0; i < Row * Row * 3; i++){
				data.writeByte(this.pic[i]);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = DefaultProperties.MOD_ID;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;
		packet.isChunkDataPacket = false;
		return packet;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return ((BlockPainting)getBlockType()).getRenderBoundingBox(worldObj, xCoord, yCoord, zCoord);
	}
}
