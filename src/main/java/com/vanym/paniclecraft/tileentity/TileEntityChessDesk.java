package com.vanym.paniclecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.vanym.paniclecraft.utils.ChessDesk;

public class TileEntityChessDesk extends TileEntity{
	
	public static final String ChessPublicPlayer = "public";
	
	public ChessDesk desk = new ChessDesk();
	
	public String whitePlayer = ChessPublicPlayer;
	
	public String blackPlayer = ChessPublicPlayer;
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setByteArray("desk", desk.desk.clone());
		par1NBTTagCompound.setByte("lastFrom", desk.lastFrom);
		par1NBTTagCompound.setByte("lastTo", desk.lastTo);
		par1NBTTagCompound.setBoolean("isWhiteTurn", desk.isWhiteTurn);
		par1NBTTagCompound.setString("whitePlayer", whitePlayer);
		par1NBTTagCompound.setString("blackPlayer", blackPlayer);
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		desk.desk = par1NBTTagCompound.getByteArray("desk");
		desk.lastFrom = par1NBTTagCompound.getByte("lastFrom");
		desk.lastTo = par1NBTTagCompound.getByte("lastTo");
		desk.isWhiteTurn = par1NBTTagCompound.getBoolean("isWhiteTurn");
		whitePlayer = par1NBTTagCompound.getString("whitePlayer");
		blackPlayer = par1NBTTagCompound.getString("blackPlayer");
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
	public AxisAlignedBB getRenderBoundingBox(){
		AxisAlignedBB var1 = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord);
		var1.maxY += 0.5D;
		return var1;
	}
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared(){
		return 16384.0D;
	}
}
