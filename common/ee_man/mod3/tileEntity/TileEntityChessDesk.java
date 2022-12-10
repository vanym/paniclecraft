package ee_man.mod3.tileEntity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.utils.ChessDesk;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityChessDesk extends TileEntity{
	
	public static final String ChessPublicPlayer = "public";
	
	public ChessDesk desk = new ChessDesk();
	
	public String name = null;
	
	public String whitePlayer = ChessPublicPlayer;
	
	public String blackPlayer = ChessPublicPlayer;
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setByteArray("desk", desk.desk);
		par1NBTTagCompound.setByte("lastFrom", desk.lastFrom);
		par1NBTTagCompound.setByte("lastTo", desk.lastTo);
		par1NBTTagCompound.setBoolean("isWhiteTurn", desk.isWhiteTurn);
		par1NBTTagCompound.setString("whitePlayer", whitePlayer);
		par1NBTTagCompound.setString("blackPlayer", blackPlayer);
		if(name != null)
			par1NBTTagCompound.setString("name", name);
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		desk.desk = par1NBTTagCompound.getByteArray("desk");
		desk.lastFrom = par1NBTTagCompound.getByte("lastFrom");
		desk.lastTo = par1NBTTagCompound.getByte("lastTo");
		desk.isWhiteTurn = par1NBTTagCompound.getBoolean("isWhiteTurn");
		whitePlayer = par1NBTTagCompound.getString("whitePlayer");
		blackPlayer = par1NBTTagCompound.getString("blackPlayer");
		if(par1NBTTagCompound.hasKey("name"))
			name = par1NBTTagCompound.getString("name");
	}
	
	public Packet getDescriptionPacket(){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try{
			data.writeByte(3);
			data.writeInt(this.xCoord);
			data.writeInt(this.yCoord);
			data.writeInt(this.zCoord);
			data.write(desk.desk);
			data.writeByte(desk.lastFrom);
			data.writeByte(desk.lastTo);
			data.writeBoolean(desk.isWhiteTurn);
			if(name == null){
				data.writeBoolean(false);
			}
			else{
				data.writeBoolean(true);
				Packet.writeString(name, data);
			}
			Packet.writeString(whitePlayer, data);
			Packet.writeString(blackPlayer, data);
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
		AxisAlignedBB var1 = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord);
		var1.maxY += 0.5D;
		return var1;
	}
}
