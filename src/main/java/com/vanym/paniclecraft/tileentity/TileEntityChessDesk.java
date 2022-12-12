package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.utils.ChessDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityChessDesk extends TileEntity {
    
    public static final String ChessPublicPlayer = "public";
    
    public ChessDesk desk = new ChessDesk();
    
    public String whitePlayer = ChessPublicPlayer;
    
    public String blackPlayer = ChessPublicPlayer;
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByteArray("desk", this.desk.desk.clone());
        par1NBTTagCompound.setByte("lastFrom", this.desk.lastFrom);
        par1NBTTagCompound.setByte("lastTo", this.desk.lastTo);
        par1NBTTagCompound.setBoolean("isWhiteTurn", this.desk.isWhiteTurn);
        par1NBTTagCompound.setString("whitePlayer", this.whitePlayer);
        par1NBTTagCompound.setString("blackPlayer", this.blackPlayer);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.desk.desk = par1NBTTagCompound.getByteArray("desk");
        this.desk.lastFrom = par1NBTTagCompound.getByte("lastFrom");
        this.desk.lastTo = par1NBTTagCompound.getByte("lastTo");
        this.desk.isWhiteTurn = par1NBTTagCompound.getBoolean("isWhiteTurn");
        this.whitePlayer = par1NBTTagCompound.getString("whitePlayer");
        this.blackPlayer = par1NBTTagCompound.getString("blackPlayer");
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound dataTag = new NBTTagCompound();
        this.writeToNBT(dataTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, dataTag);
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        NBTTagCompound nbtData = packet.func_148857_g();
        this.readFromNBT(nbtData);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB var1 =
                this.getBlockType()
                    .getCollisionBoundingBoxFromPool(this.worldObj, this.xCoord, this.yCoord,
                                                     this.zCoord);
        var1.maxY += 0.5D;
        return var1;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
}
