package com.vanym.paniclecraft.entity;

import java.util.ArrayList;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.IPictureHolder;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.DataWatcher.WatchableObject;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import scala.actors.threadpool.Arrays;

public class EntityPaintOnBlock extends Entity implements ISidePictureProvider {
    
    public static final String IN_MOD_ID = "paintonblock";
    
    public static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
    public static final int N = TileEntityPaintingFrame.N;
    
    protected final PictureHolder[] holders = new PictureHolder[N];
    
    protected final PaintOnBlockWatcher picturesWatcher = new PaintOnBlockWatcher();
    
    public EntityPaintOnBlock(World world) {
        super(world);
        this.yOffset = 0.0F;
        this.ySize = 0.0F;
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
        
        DataWatcher orig = this.dataWatcher;
        this.dataWatcher = this.picturesWatcher;
        @SuppressWarnings("rawtypes")
        List list = orig.getAllWatched();
        if (list != null) {
            for (Object obj : list) {
                DataWatcher.WatchableObject wo = (WatchableObject)obj;
                this.dataWatcher.addObjectByDataType(wo.getDataValueId(), wo.getObjectType());
                this.dataWatcher.updateObject(wo.getDataValueId(), wo.getObject());
            }
        }
    }
    
    public int getBlockX() {
        return MathHelper.floor_double(this.posX);
    }
    
    public int getBlockY() {
        return MathHelper.floor_double(this.posY);
    }
    
    public int getBlockZ() {
        return MathHelper.floor_double(this.posZ);
    }
    
    public void setBlock(int x, int y, int z) {
        this.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
    }
    
    public Picture createPicture(int side) {
        if (!this.isValidSide(side)) {
            return null;
        }
        if (this.holders[side] != null) {
            return this.holders[side].picture;
        } else {
            return (this.holders[side] = new PictureHolder(side)).picture;
        }
    }
    
    public boolean clearPicture(int side) {
        if (!this.isValidSide(side) || this.holders[side] == null) {
            return false;
        }
        this.holders[side].picture.unload();
        this.holders[side] = null;
        return true;
    }
    
    @Override
    public Picture getPicture(int side) {
        return this.createPicture(side);
    }
    
    public Picture getExistingPicture(int side) {
        if (this.isValidSide(side) && this.holders[side] != null) {
            return this.holders[side].picture;
        }
        return null;
    }
    
    protected boolean isValidSide(int side) {
        return side >= 0 && side < this.holders.length;
    }
    
    @Override
    protected void entityInit() {}
    
    protected void unloadPictures() {
        for (PictureHolder holder : this.holders) {
            if (holder != null) {
                holder.picture.unload();
            }
        }
    }
    
    public void onEntityDestroy() {
        this.unloadPictures();
    }
    
    public void onWorldUnload() {
        this.unloadPictures();
    }
    
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        if (!isValidBlock(this.worldObj, this.getBlockX(), this.getBlockY(), this.getBlockZ())) {
            this.setDead();
        }
    }
    
    @Override
    public void moveEntity(double x, double y, double z) {}
    
    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            int i) {
        this.setPositionAndRotation(x, y, z, yaw, pitch);
    }
    
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTag) {
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (this.holders[i] != null) {
                NBTTagCompound pictureTag = new NBTTagCompound();
                this.holders[i].picture.writeToNBT(pictureTag);
                nbtTag.setTag(TAG_PICTURE_I, pictureTag);
            }
        }
    }
    
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTag) {
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (nbtTag.hasKey(TAG_PICTURE_I)) {
                Picture picture = this.createPicture(i);
                picture.readFromNBT(nbtTag.getCompoundTag(TAG_PICTURE_I));
            } else {
                this.clearPicture(i);
            }
            this.picturesWatcher.setPictureWatched(i);
        }
    }
    
    protected class PictureHolder implements IPictureHolder {
        
        protected final Picture picture = new Picture(this, true);
        
        protected int side;
        
        public PictureHolder(int side) {
            this.side = side;
        }
        
        public void setSide(int side) {
            this.side = side;
        }
        
        @Override
        public Picture getNeighborPicture(int offsetX, int offsetY) {
            PaintingSide pside = PaintingSide.getSize(this.side);
            int x = EntityPaintOnBlock.this.getBlockX() +
                    pside.xDir.offsetX * offsetX +
                    pside.yDir.offsetX * offsetY;
            int y = EntityPaintOnBlock.this.getBlockY() +
                    pside.xDir.offsetY * offsetX +
                    pside.yDir.offsetY * offsetY;
            int z = EntityPaintOnBlock.this.getBlockZ() +
                    pside.xDir.offsetZ * offsetX +
                    pside.yDir.offsetZ * offsetY;
            EntityPaintOnBlock entityPOB =
                    getOrCreateEntity(EntityPaintOnBlock.this.worldObj, x, y, z);
            if (entityPOB == null) {
                return null;
            }
            return entityPOB.getPicture(this.side);
        }
        
        @Override
        public void update() {
            EntityPaintOnBlock.this.picturesWatcher.setPictureWatched(this.side);
        }
    }
    
    protected class PaintOnBlockWatcher extends DataWatcher {
        
        protected final int pictureWatchOffset = 16;
        
        protected final WatchablePicture[] watchablePictures =
                new WatchablePicture[EntityPaintOnBlock.this.holders.length];
        
        public PaintOnBlockWatcher() {
            super(EntityPaintOnBlock.this);
            for (int i = 0; i < this.watchablePictures.length; ++i) {
                this.watchablePictures[i] = new WatchablePicture(this.pictureWatchOffset + i, i);
            }
        }
        
        @Override
        public boolean getIsBlank() {
            return false;
        }
        
        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public List getAllWatched() {
            List list = super.getAllWatched();
            if (list == null) {
                list = new ArrayList<>();
            }
            list.addAll(Arrays.asList(this.watchablePictures));
            return list;
        }
        
        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public List getChanged() {
            List list = super.getChanged();
            if (list == null) {
                list = new ArrayList<>();
            }
            for (WatchablePicture wp : this.watchablePictures) {
                if (wp.isWatched()) {
                    wp.setWatched(false);
                    list.add(wp);
                }
            }
            if (list.isEmpty()) {
                list = null;
            }
            return list;
        }
        
        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings("rawtypes")
        public void updateWatchedObjectsFromList(List list) {
            super.updateWatchedObjectsFromList(list);
            for (Object obj : list) {
                WatchableObject wo = (WatchableObject)obj;
                int side = wo.getDataValueId() - this.pictureWatchOffset;
                if (EntityPaintOnBlock.this.isValidSide(side)) {
                    this.watchablePictures[side].setObject(wo.getObject());
                    EntityPaintOnBlock.this.func_145781_i(wo.getDataValueId());
                }
            }
        }
        
        @Override
        public boolean hasChanges() {
            if (super.hasChanges()) {
                return true;
            }
            for (WatchablePicture wp : this.watchablePictures) {
                if (wp.isWatched()) {
                    return true;
                }
            }
            return false;
        }
        
        public void setPictureWatched(int side) {
            if (!EntityPaintOnBlock.this.isValidSide(side)) {
                return;
            }
            this.watchablePictures[side].setWatched(true);
        }
        
        protected class WatchablePicture extends DataWatcher.WatchableObject {
            
            protected final int side;
            
            public WatchablePicture(int dataValueId, int side) {
                super(5, dataValueId, null);
                this.side = side;
            }
            
            @Override
            public void setObject(Object obj) {
                ItemStack stack = (ItemStack)obj;
                if (stack.hasTagCompound()) {
                    Picture picture = EntityPaintOnBlock.this.createPicture(this.side);
                    if (Core.instance.painting.itemPainting.fillPicture(picture, stack)) {
                        return;
                    }
                }
                EntityPaintOnBlock.this.clearPicture(this.side);
            }
            
            @Override
            public Object getObject() {
                return BlockPaintingContainer.getPictureAsItem(EntityPaintOnBlock.this.getExistingPicture(this.side));
            }
        }
    }
    
    public static boolean isValidBlock(World world, int x, int y, int z) {
        return !world.isAirBlock(x, y, z);
    }
    
    public static EntityPaintOnBlock getOrCreateEntity(World world, int x, int y, int z) {
        EntityPaintOnBlock entityPON = getEntity(world, x, y, z);
        if (entityPON != null) {
            return entityPON;
        }
        if (world.isRemote || !isValidBlock(world, x, y, z)) {
            return null;
        }
        entityPON = new EntityPaintOnBlock(world);
        entityPON.setBlock(x, y, z);
        if (world.spawnEntityInWorld(entityPON)) {
            return entityPON;
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static EntityPaintOnBlock getEntity(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) {
            return null;
        }
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        int listIndex = y / 16;
        if (listIndex < 0 || listIndex >= chunk.entityLists.length) {
            return null;
        }
        List list = chunk.entityLists[listIndex];
        for (Object entity : list) {
            if (entity instanceof EntityPaintOnBlock) {
                EntityPaintOnBlock entityPOB = (EntityPaintOnBlock)entity;
                if (x == entityPOB.getBlockX()
                    && y == entityPOB.getBlockY()
                    && z == entityPOB.getBlockZ()) {
                    return entityPOB;
                }
            }
        }
        return null;
    }
    
    public static Picture getExistingPicture(World world, int x, int y, int z, int side) {
        EntityPaintOnBlock entityPOB = getEntity(world, x, y, z);
        if (entityPOB == null) {
            return null;
        }
        return entityPOB.getExistingPicture(side);
    }
}
