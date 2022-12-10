package ee_man.mod3.tileEntity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityAdvSign extends TileEntity{
	public static char separatorChar = '\n';
	
	public static String separator = Character.toString(separatorChar);
	
	public static boolean isAddedToTileMap = false;
	
	/** An array of four strings storing the lines of text on the sign. */
	public String signText = separator + separator + separator + separator;
	
	/**
	 * The index of the line currently being edited. Only used on client side,
	 * but defined on both. Note this is only really used when the > < are going
	 * to be visible.
	 */
	// @SideOnly(Side.CLIENT)
	public int lineBeingEdited = -1;
	
	public boolean isEditable = true;
	
	public boolean canBeEdit;
	
	public byte red = 127;
	
	public byte green = 127;
	
	public byte blue = 127;
	
	public TileEntityAdvSign(){
		this(true);
	}
	
	public TileEntityAdvSign(boolean par1){
		canBeEdit = par1;
	}
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setString("SignText", this.signText);
		par1NBTTagCompound.setBoolean("canBeEdit", canBeEdit);
		par1NBTTagCompound.setByte("red", red);
		par1NBTTagCompound.setByte("green", green);
		par1NBTTagCompound.setByte("blue", blue);
	}
	
	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		this.isEditable = false;
		super.readFromNBT(par1NBTTagCompound);
		this.signText = par1NBTTagCompound.getString("SignText");
		this.canBeEdit = par1NBTTagCompound.getBoolean("canBeEdit");
		this.red = par1NBTTagCompound.getByte("red");
		this.green = par1NBTTagCompound.getByte("green");
		this.blue = par1NBTTagCompound.getByte("blue");
	}
	
	/**
	 * Overriden in a sign to provide the text.
	 */
	
	public Packet getDescriptionPacket(){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try{
			data.writeByte(1);
			data.writeInt(this.xCoord);
			data.writeInt(this.yCoord);
			data.writeInt(this.zCoord);
			data.writeByte(this.red);
			data.writeByte(this.green);
			data.writeByte(this.blue);
			Packet.writeString(this.signText, data);
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
	
	/*
	 * public Packet getDescriptionPacket(){ NBTTagCompound tag = new
	 * NBTTagCompound(); this.writeToNBT(tag); return new
	 * Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag); }
	 * 
	 * public void onDataPacket(INetworkManager net, Packet132TileEntityData
	 * packet) { readFromNBT(packet.customParam1); }
	 */
	public boolean isEditable(){
		return this.isEditable;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getAABBPool().getAABB((double)this.xCoord - 0.0F, (double)this.yCoord + 0.0F, (double)this.zCoord - 0.0F, (double)this.xCoord + 1.0F, (double)this.yCoord + 1.0F, (double)this.zCoord + 1.0F);
	}
	
	// @SideOnly(Side.CLIENT)
	
	/**
	 * Sets the sign's isEditable flag to the specified parameter.
	 */
	public void setEditable(boolean par1){
		this.isEditable = par1;
	}
	
	public void editLine(int par1, String par2){
		String[] var1 = this.signText.split(separator, this.getLines());
		var1[par1] = par2;
		String var3 = "";
		for(int var2 = 0; var2 < this.getLines(); ++var2){
			var3 = var3 + var1[var2];
			if(var2 != this.getLines() - 1)
				var3 = var3 + separator;
		}
		this.signText = var3;
	}
	
	public String getLine(int par1){
		String[] var1 = this.signText.split(separator, this.getLines());
		return var1[par1];
	}
	
	public boolean canBeEdit(){
		return canBeEdit;
	}
	
	public int getLines(){
		return (this.signText + '\u0000').split(separator).length;
	}
}
