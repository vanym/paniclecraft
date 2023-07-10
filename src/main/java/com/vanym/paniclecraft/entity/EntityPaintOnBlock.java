package com.vanym.paniclecraft.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.IPictureHolder;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.DataWatcher.WatchableObject;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

public class EntityPaintOnBlock extends Entity implements ISidePictureProvider {
    
    public static final String IN_MOD_ID = "paintonblock";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    public static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
    protected final PictureHolder[] holders = new PictureHolder[N];
    
    protected final PaintOnBlockWatcher picturesWatcher = new PaintOnBlockWatcher();
    
    protected boolean proceeded = false;
    
    public EntityPaintOnBlock(World world) {
        super(world);
        this.yOffset = 0.0F;
        this.ySize = 0.0F;
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.renderDistanceWeight = 4.0D;
        
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
    
    public void checkValidness() {
        for (int i = 0; i < this.holders.length; ++i) {
            if ((this.holders[i] == null)
                || EntityPaintOnBlock.isValidBlockSide(this.worldObj,
                                                       this.getBlockX(),
                                                       this.getBlockY(),
                                                       this.getBlockZ(), i)) {
                continue;
            }
            this.clearPicture(i);
        }
    }
    
    protected PictureHolder createHolder(int side) {
        if (!this.isValidSide(side)) {
            return null;
        }
        if (this.holders[side] != null) {
            return this.holders[side];
        } else {
            this.needProceed(); // to remove if it will stay empty
            return this.holders[side] = new PictureHolder(side);
        }
    }
    
    public Picture createPicture(int side) {
        PictureHolder holder = this.createHolder(side);
        if (holder != null) {
            return holder.picture;
        } else {
            return null;
        }
    }
    
    public boolean clearPicture(int side) {
        if (!this.isValidSide(side) || this.holders[side] == null) {
            return false;
        }
        this.holders[side].picture.unload();
        this.holders[side] = null;
        this.needProceed();
        return true;
    }
    
    @Override
    public Picture getPicture(int side) {
        if (this.isValidSide(side) && this.holders[side] != null) {
            return this.holders[side].picture;
        }
        return null;
    }
    
    protected boolean isValidSide(int side) {
        return side >= 0 && side < this.holders.length;
    }
    
    protected void clearEmpty() {
        for (int i = 0; i < this.holders.length; ++i) {
            if (this.holders[i] != null && this.holders[i].empty) {
                this.clearPicture(i);
            }
        }
    }
    
    protected boolean isEmpty() {
        return Arrays.stream(this.holders).allMatch(h->h == null || h.empty);
    }
    
    protected boolean killIfEmpty() {
        if (this.isEmpty()) {
            this.setDead();
            return true;
        }
        return false;
    }
    
    protected void needProceed() {
        this.proceeded = false;
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
        if (!this.worldObj.isRemote && !this.proceeded) {
            this.clearEmpty();
            this.killIfEmpty();
            this.proceeded = true;
        }
    }
    
    @Override
    public void moveEntity(double x, double y, double z) {}
    
    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }
    
    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    
    @Override
    public boolean isPushedByWater() {
        return false;
    }
    
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
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double dist) {
        return dist <= Core.instance.painting.clientConfig.renderPaintOnBlockMaxRenderDistanceSquared;
    }
    
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTag) {
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (this.holders[i] != null) {
                nbtTag.setTag(TAG_PICTURE_I, this.holders[i].picture.serializeNBT());
            }
        }
    }
    
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTag) {
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (nbtTag.hasKey(TAG_PICTURE_I)) {
                PictureHolder holder = this.createHolder(i);
                holder.picture.deserializeNBT(nbtTag.getCompoundTag(TAG_PICTURE_I));
                holder.empty = false;
            } else {
                this.clearPicture(i);
            }
            this.picturesWatcher.setPictureWatched(i);
        }
    }
    
    protected class PictureHolder implements IPictureHolder {
        
        protected final Picture picture = new Picture(this, true);
        
        protected int side;
        
        protected boolean empty = true;
        
        public PictureHolder(int side) {
            this.side = side;
        }
        
        public void setSide(int side) {
            this.side = side;
        }
        
        @Override
        public IPictureSize getDefaultSize() {
            return Core.instance.painting.config.paintOnBlockDefaultSize;
        }
        
        @Override
        public Picture getNeighborPicture(int offsetX, int offsetY) {
            return new WorldPicturePoint(
                    WorldPictureProvider.PAINTONBLOCK,
                    EntityPaintOnBlock.this.worldObj,
                    EntityPaintOnBlock.this.getBlockX(),
                    EntityPaintOnBlock.this.getBlockY(),
                    EntityPaintOnBlock.this.getBlockZ(),
                    this.side).getNeighborPoint(offsetX, offsetY).getOrCreatePicture();
        }
        
        @Override
        public void update() {
            EntityPaintOnBlock.this.picturesWatcher.setPictureWatched(this.side);
            if (this.picture.isEmpty()) {
                EntityPaintOnBlock.this.clearPicture(this.side);
                EntityPaintOnBlock.this.needProceed();
            } else {
                this.empty = false;
            }
        }
        
        @Override
        public String toString() {
            return String.format("PaintOnBlock[x=%d, y=%d, z=%d, side=%s]",
                                 EntityPaintOnBlock.this.getBlockX(),
                                 EntityPaintOnBlock.this.getBlockY(),
                                 EntityPaintOnBlock.this.getBlockZ(),
                                 ForgeDirection.getOrientation(this.side));
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
                    PictureHolder holder = EntityPaintOnBlock.this.createHolder(this.side);
                    if (ItemPainting.fillPicture(holder.picture, stack)) {
                        holder.empty = false;
                        return;
                    }
                }
                EntityPaintOnBlock.this.clearPicture(this.side);
            }
            
            @Override
            public Object getObject() {
                return ItemPainting.getPictureAsItem(EntityPaintOnBlock.this.getPicture(this.side));
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public static int clearArea(World world, AxisAlignedBB box) {
        List<Entity> list = world.getEntitiesWithinAABB(EntityPaintOnBlock.class, box);
        list.forEach(e->e.setDead());
        return list.size();
    }
    
    public static Picture getOrCreateEntityPicture(World world, int x, int y, int z, int side) {
        EntityPaintOnBlock entityPON = getEntity(world, x, y, z);
        if (entityPON != null) {
            Picture picture = entityPON.getPicture(side);
            if (!world.isRemote && picture == null
                && isValidBlockSide(world, x, y, z, side)) {
                picture = entityPON.createPicture(side);
            }
            return picture;
        }
        if (world.isRemote || !isValidBlockSide(world, x, y, z, side)) {
            return null;
        }
        entityPON = new EntityPaintOnBlock(world);
        entityPON.setBlock(x, y, z);
        if (world.spawnEntityInWorld(entityPON)) {
            return entityPON.createPicture(side);
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
        return entityPOB.getPicture(side);
    }
    
    public static boolean isValidBlockSide(World world, int x, int y, int z, int side) {
        boolean valid;
        boolean air = false;
        boolean liquid = false;
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection pside = ForgeDirection.getOrientation(side);
        if (block.isAir(world, x, y, z)) {
            valid = false;
            air = true;
        } else if (block.getMaterial().isLiquid()) {
            valid = false;
            liquid = true;
        } else if (block.isOpaqueCube()) {
            Block neighbor =
                    world.getBlock(x + pside.offsetX, y + pside.offsetY, z + pside.offsetZ);
            if (neighbor.isOpaqueCube()) {
                valid = false;
            } else {
                valid = true;
            }
        } else {
            switch (block.getRenderType()) {
                case 0: // StandardBlock
                case 31: // BlockLog
                case 39: // BlockQuartz
                case 34: // BlockBeacon
                    valid = true;
                break;
                case 13: // BlockCactus
                case 14: // BlockBed
                case 24: // BlockCauldron
                case 35: // BlockAnvil
                case 26: // BlockEndPortalFrame
                    valid = true;
                break;
                case 38: // BlockHopper
                    valid = (pside == ForgeDirection.UP);
                break;
                case 20: // BlockVine
                case 5: // BlockRedstoneWire
                case 23: // BlockLilyPad
                case 8: // BlockLadder
                case 30: // BlockTripWire
                case 25: // BlockBrewingStand
                    valid = true;
                break;
                case 10: // BlockStairs
                    valid = true;
                break;
                case 16: // PistonBase
                case 17: // PistonExtension
                    valid = true;
                break;
                case 22: // BlockChest
                    valid = true;
                break;
                case 9: // BlockMinecartTrack
                    switch (meta & 0b111) {
                        case 2:
                        case 4:
                        case 3:
                        case 5:
                            valid = false;
                        break;
                        default:
                            valid = true;
                        break;
                    }
                break;
                default:
                    valid = false;
                break;
            }
        }
        BlockSideValidForPaint event =
                new BlockSideValidForPaint(x, y, z, world, block, meta, side, valid, air, liquid);
        MinecraftForge.EVENT_BUS.post(event);
        switch (event.getResult()) {
            case ALLOW:
                valid = true;
            break;
            case DENY:
                valid = false;
            break;
            case DEFAULT:
            default:
            break;
        }
        return valid;
    }
    
    @Event.HasResult
    public static class BlockSideValidForPaint extends BlockEvent {
        
        public final int side;
        public final boolean valid;
        public final boolean air;
        public final boolean liquid;
        
        public BlockSideValidForPaint(int x,
                int y,
                int z,
                World world,
                Block block,
                int meta,
                int side,
                boolean valid,
                boolean air,
                boolean liquid) {
            super(x, y, z, world, block, meta);
            this.side = side;
            this.valid = valid;
            this.air = air;
            this.liquid = liquid;
        }
    }
}
