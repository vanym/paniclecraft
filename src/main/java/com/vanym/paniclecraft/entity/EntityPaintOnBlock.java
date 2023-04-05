package com.vanym.paniclecraft.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

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

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPaintOnBlock extends Entity implements ISidePictureProvider {
    
    public static final String IN_MOD_ID = "paintonblock";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    public static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
    protected static final int PICTURE_PARAMETER_OFFSET = 16;
    protected static final PictureParameter[] PICTURE_PARAMETERS;
    
    static {
        PICTURE_PARAMETERS = new PictureParameter[N];
        for (int i = 0; i < N; ++i) {
            PICTURE_PARAMETERS[i] = new PictureParameter(PICTURE_PARAMETER_OFFSET + i, i);
        }
    }
    
    protected final PictureHolder[] holders = new PictureHolder[N];
    
    protected final PaintOnBlockDataManager picturesDataManager = new PaintOnBlockDataManager();
    
    protected boolean proceeded = false;
    
    public EntityPaintOnBlock(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.setEntityInvulnerable(true);
        
        EntityDataManager orig = this.dataManager;
        this.dataManager = this.picturesDataManager;
        List<EntityDataManager.DataEntry<?>> list = orig.getAll();
        if (list != null) {
            for (EntityDataManager.DataEntry<?> entry : list) {
                registerByEntry(this.dataManager, entry);
            }
        }
    }
    
    public int getBlockX() {
        return MathHelper.floor(this.posX);
    }
    
    public int getBlockY() {
        return MathHelper.floor(this.posY);
    }
    
    public int getBlockZ() {
        return MathHelper.floor(this.posZ);
    }
    
    public BlockPos getBlockPos() {
        return new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }
    
    public void setBlock(int x, int y, int z) {
        this.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
    }
    
    public void checkValidness() {
        for (int i = 0; i < this.holders.length; ++i) {
            if ((this.holders[i] == null)
                || EntityPaintOnBlock.isValidBlockSide(this.world,
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
        if (!this.world.isRemote && !this.proceeded) {
            this.clearEmpty();
            this.killIfEmpty();
            this.proceeded = true;
        }
    }
    
    @Override
    public void move(MoverType type, double x, double y, double z) {}
    
    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
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
    public boolean isInRangeToRenderDist(double dist) {
        return dist <= Core.instance.painting.clientConfig.renderPaintOnBlockMaxRenderDistanceSquared;
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
                PictureHolder holder = this.createHolder(i);
                holder.picture.readFromNBT(nbtTag.getCompoundTag(TAG_PICTURE_I));
                holder.empty = false;
            } else {
                this.clearPicture(i);
            }
            this.picturesDataManager.setPictureDirty(i);
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
                    EntityPaintOnBlock.this.world,
                    EntityPaintOnBlock.this.getBlockX(),
                    EntityPaintOnBlock.this.getBlockY(),
                    EntityPaintOnBlock.this.getBlockZ(),
                    this.side).getNeighborPoint(offsetX, offsetY).getOrCreatePicture();
        }
        
        @Override
        public void update() {
            EntityPaintOnBlock.this.picturesDataManager.setPictureDirty(this.side);
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
                                 EnumFacing.getFront(this.side));
        }
    }
    
    protected class PaintOnBlockDataManager extends EntityDataManager {
        
        protected final PictureEntry[] pictureEntries =
                new PictureEntry[PICTURE_PARAMETERS.length];
        
        public PaintOnBlockDataManager() {
            super(EntityPaintOnBlock.this);
            for (int i = 0; i < this.pictureEntries.length; ++i) {
                this.pictureEntries[i] = new PictureEntry(PICTURE_PARAMETERS[i]);
            }
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(DataParameter<T> key) {
            if (key instanceof PictureParameter) {
                PictureParameter picKey = (PictureParameter)key;
                return (T)this.pictureEntries[picKey.side].getValue();
            } else {
                return super.get(key);
            }
        }
        
        @Override
        public <T> void set(DataParameter<T> key, T value) {
            if (key instanceof PictureParameter) {
                PictureParameter picKey = (PictureParameter)key;
                this.pictureEntries[picKey.side].setValue((ItemStack)value);
            } else {
                super.set(key, value);
            }
        }
        
        public void setPictureDirty(int side) {
            this.pictureEntries[side].setDirty(true);
        }
        
        @Override
        public <T> void setDirty(DataParameter<T> key) {
            if (key instanceof PictureParameter) {
                PictureParameter picKey = (PictureParameter)key;
                this.pictureEntries[picKey.side].setDirty(true);
            } else {
                super.setDirty(key);
            }
        }
        
        @Override
        public boolean isDirty() {
            return super.isDirty()
                || Arrays.stream(this.pictureEntries).anyMatch(PictureEntry::isDirty);
        }
        
        @Override
        public void setClean() {
            super.setClean();
            Arrays.stream(this.pictureEntries).forEach(p->p.setDirty(false));
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        @Nullable
        public List<EntityDataManager.DataEntry<?>> getDirty() {
            List<EntityDataManager.DataEntry<?>> list = super.getDirty();
            if (list == null) {
                list = new ArrayList<>();
            }
            for (PictureEntry pictureEntry : this.pictureEntries) {
                if (pictureEntry.isDirty()) {
                    pictureEntry.setDirty(false);
                    list.add(pictureEntry.copy());
                }
            }
            if (list.isEmpty()) {
                list = null;
            }
            return list;
        }
        
        @Override
        @Nullable
        public List<EntityDataManager.DataEntry<?>> getAll() {
            List<EntityDataManager.DataEntry<?>> list = super.getAll();
            if (list == null) {
                list = new ArrayList<>();
            }
            for (PictureEntry pictureEntry : this.pictureEntries) {
                list.add(pictureEntry.copy());
            }
            return list;
        }
        
        @Override
        public void writeEntries(PacketBuffer buf) throws IOException {
            super.writeEntries(buf);
            // removing '255' end byte
            buf.writerIndex(buf.writerIndex() - 1);
            writeEntries(Arrays.asList(this.pictureEntries), buf);
        }
        
        @Override
        public void setEntryValues(List<EntityDataManager.DataEntry<?>> entries) {
            super.setEntryValues(entries);
            for (EntityDataManager.DataEntry<?> entry : entries) {
                Optional<PictureParameter> oKey = getPictureParameterByKey(entry.getKey());
                if (!oKey.isPresent()) {
                    continue;
                }
                PictureParameter key = oKey.get();
                this.setEntryValue(this.pictureEntries[key.side], entry);
                EntityPaintOnBlock.this.notifyDataManagerChange(key);
            }
        }
        
        protected class PictureEntry extends EntityDataManager.DataEntry<ItemStack> {
            
            protected final int side;
            
            public PictureEntry(PictureParameter key) {
                super(key, ItemStack.EMPTY);
                this.side = key.side;
            }
            
            @Override
            public void setValue(ItemStack stack) {
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
            public ItemStack getValue() {
                return ItemPainting.getPictureAsItem(EntityPaintOnBlock.this.getPicture(this.side));
            }
            
            @Override
            public EntityDataManager.DataEntry<ItemStack> copy() {
                return new EntityDataManager.DataEntry<>(this.getKey(), this.getValue());
            }
        }
    }
    
    protected static class PictureParameter extends DataParameter<ItemStack> {
        
        public final int side;
        
        public PictureParameter(int id, int side) {
            super(id, DataSerializers.ITEM_STACK);
            this.side = side;
        }
    }
    
    protected static Optional<PictureParameter> getPictureParameterByKey(DataParameter<?> key) {
        return Arrays.stream(PICTURE_PARAMETERS)
                     .filter(e->e.getId() == key.getId())
                     .findAny();
    }
    
    protected static <T> void registerByEntry(
            EntityDataManager dataManager,
            EntityDataManager.DataEntry<T> entry) {
        dataManager.register(entry.getKey(), entry.getValue());
    }
    
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
        if (world.spawnEntity(entityPON)) {
            return entityPON.createPicture(side);
        } else {
            return null;
        }
    }
    
    public static EntityPaintOnBlock getEntity(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isBlockLoaded(pos)) {
            return null;
        }
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        ClassInheritanceMultiMap<Entity>[] lists = chunk.getEntityLists();
        int listIndex = pos.getY() / 16;
        if (listIndex < 0 || listIndex >= lists.length) {
            return null;
        }
        ClassInheritanceMultiMap<Entity> list = lists[listIndex];
        for (EntityPaintOnBlock entityPOB : list.getByClass(EntityPaintOnBlock.class)) {
            if (pos.getX() == entityPOB.getBlockX()
                && pos.getY() == entityPOB.getBlockY()
                && pos.getZ() == entityPOB.getBlockZ()) {
                return entityPOB;
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
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        EnumFacing pside = EnumFacing.getFront(side);
        if (state.getBlock().isAir(state, world, pos)) {
            valid = false;
            air = true;
        } else if (state.getMaterial().isLiquid()) {
            valid = false;
            liquid = true;
        } else if (state.isOpaqueCube()) {
            IBlockState neighborState = world.getBlockState(pos.offset(pside));
            if (neighborState.isOpaqueCube()) {
                valid = false;
            } else {
                valid = true;
            }
        } else {
            // TODO make detailed rules
            valid = false;
        }
        BlockSideValidForPaint event =
                new BlockSideValidForPaint(world, pos, state, side, valid, air, liquid);
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
        
        public BlockSideValidForPaint(
                World world,
                BlockPos pos,
                IBlockState state,
                int side,
                boolean valid,
                boolean air,
                boolean liquid) {
            super(world, pos, state);
            this.side = side;
            this.valid = valid;
            this.air = air;
            this.liquid = liquid;
        }
    }
}
