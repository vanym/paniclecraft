package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.utils.ISidePaintingProvider;
import com.vanym.paniclecraft.utils.Painting;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPainting extends TileEntity implements ISidePaintingProvider {
    
    private boolean need_to_update = false;
    
    private Painting picture;
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        NBTTagCompound paintingTag = new NBTTagCompound();
        this.getPicture().writeToNBT(paintingTag);
        par1NBTTagCompound.setTag("PictureData", paintingTag);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("PictureData")) {
            this.getPicture().readFromNBT(par1NBTTagCompound.getCompoundTag("PictureData"));
        } else {
            this.picture = null;
        }
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
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
    
    private Painting getPicture() {
        if (this.picture == null) {
            this.picture = new Painting(this);
        }
        return this.picture;
    }
    
    @Override
    public Painting getPainting(int side) {
        if (side == this.getBlockMetadata()) {
            return this.getPicture();
        } else {
            return null;
        }
    }
    
    @Override
    public Painting getPainting(int side, int xO, int yO) {
        int x = this.xCoord;
        int y = this.yCoord;
        int z = this.zCoord;
        switch (side) {
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
        if (var1 instanceof TileEntityPainting
            && ((TileEntityPainting)var1).getPicture().getRow() == this.getPicture().getRow()
            && var1.getBlockMetadata() == this.getBlockMetadata()) {
            return ((TileEntityPainting)var1).getPainting(side);
        }
        return null;
    }
    
    @Override
    public void needUpdate() {
        this.need_to_update = true;
    }
    
    @Override
    public void updateEntity() {
        super.updateEntity();
        if (this.need_to_update) {
            this.markForUpdate();
        }
    }
    
    @Override
    public void markForUpdate() {
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.need_to_update = false;
    }
    
    @Override
    public int getPictureSide(Painting picture) {
        if (this.getPicture().equals(picture)) {
            return this.getBlockMetadata();
        } else {
            return -1;
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void invalidate() {
        super.invalidate();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.getPicture().deleteTexrure();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onChunkUnload() {
        super.onChunkUnload();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.getPicture().deleteTexrure();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onWorldUnload() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.getPicture().deleteTexrure();
        }
    }
}
