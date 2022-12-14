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

public class TileEntityPaintingFrame extends TileEntity implements ISidePaintingProvider {
    private boolean need_to_update = false;
    private final Painting[] paintings = new Painting[6];
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        for (int i = 0; i < this.paintings.length; i++) {
            if (this.getPainting(i) != null) {
                NBTTagCompound paintingTag = new NBTTagCompound();
                this.getPainting(i).writeToNBT(paintingTag);
                par1NBTTagCompound.setTag("PictureData[" + i + "]", paintingTag);
            }
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        for (int i = 0; i < this.paintings.length; i++) {
            if (par1NBTTagCompound.hasKey("PictureData[" + i + "]")) {
                if (this.getPainting(i) == null) {
                    this.paintings[i] = new Painting(this);
                }
                this.getPainting(i)
                    .readFromNBT(par1NBTTagCompound.getCompoundTag("PictureData[" + i + "]"));
            } else {
                this.paintings[i] = null;
            }
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
    
    public void setPainting(int side, Painting picture) {
        this.paintings[side] = picture;
        this.markForUpdate();
    }
    
    public Painting[] getPaintings() {
        return this.paintings;
    }
    
    @Override
    public Painting getPainting(int side) {
        if (side >= 0 && side < this.paintings.length) {
            return this.paintings[side];
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
        if (var1 instanceof TileEntityPaintingFrame
            && ((TileEntityPaintingFrame)var1).getPainting(side) != null
            && ((TileEntityPaintingFrame)var1).getPainting(side).getRow() == this.getPainting(side)
                                                                                 .getRow()) {
            return ((TileEntityPaintingFrame)var1).getPainting(side);
        }
        return null;
    }
    
    @Override
    public int getPictureSide(Painting picture) {
        for (int i = 0; i < this.paintings.length; i++) {
            if (picture.equals(this.paintings[i])) {
                return i;
            }
        }
        return -1;
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
    public void needUpdate() {
        this.need_to_update = true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void invalidate() {
        super.invalidate();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            for (int i = 0; i < this.paintings.length; i++) {
                if (this.paintings[i] != null) {
                    this.paintings[i].deleteTexrure();
                }
            }
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onChunkUnload() {
        super.onChunkUnload();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            for (int i = 0; i < this.paintings.length; i++) {
                if (this.paintings[i] != null) {
                    this.paintings[i].deleteTexrure();
                }
            }
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onWorldUnload() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            for (int i = 0; i < this.paintings.length; i++) {
                if (this.paintings[i] != null) {
                    this.paintings[i].deleteTexrure();
                }
            }
        }
    }
    
}
