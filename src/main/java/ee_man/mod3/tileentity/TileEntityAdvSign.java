package ee_man.mod3.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	
	public Packet getDescriptionPacket(){
		NBTTagCompound dataTag = new NBTTagCompound();
		this.writeToNBT(dataTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, dataTag);
	}
	
	public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet){
		NBTTagCompound nbtData = packet.func_148857_g();
		this.readFromNBT(nbtData);
	}
	
	public boolean isEditable(){
		return this.isEditable;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getBoundingBox((double)this.xCoord - 0.0F, (double)this.yCoord + 0.0F, (double)this.zCoord - 0.0F, (double)this.xCoord + 1.0F, (double)this.yCoord + 1.0F, (double)this.zCoord + 1.0F);
	}
	
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
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared(){
		return 16384.0D;
	}
}
