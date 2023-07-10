package com.vanym.paniclecraft.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
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
    
    public BlockPos getBlockPos() {
        return new BlockPos(this);
    }
    
    public void setBlockPos(BlockPos pos) {
        this.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
    }
    
    public void checkValidness() {
        for (int i = 0; i < this.holders.length; ++i) {
            if ((this.holders[i] == null)
                || EntityPaintOnBlock.isValidBlockSide(this.world, this.getBlockPos(), i)) {
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
    public EnumPushReaction getPushReaction() {
        return EnumPushReaction.IGNORE;
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
                    EntityPaintOnBlock.this.getBlockPos(),
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
            BlockPos pos = EntityPaintOnBlock.this.getBlockPos();
            return String.format("PaintOnBlock[x=%d, y=%d, z=%d, side=%s]",
                                 pos.getX(), pos.getY(), pos.getZ(),
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
    
    public static Picture getOrCreateEntityPicture(World world, BlockPos pos, int side) {
        EntityPaintOnBlock entityPON = getEntity(world, pos);
        if (entityPON != null) {
            Picture picture = entityPON.getPicture(side);
            if (!world.isRemote && picture == null
                && isValidBlockSide(world, pos, side)) {
                picture = entityPON.createPicture(side);
            }
            return picture;
        }
        if (world.isRemote || !isValidBlockSide(world, pos, side)) {
            return null;
        }
        entityPON = new EntityPaintOnBlock(world);
        entityPON.setBlockPos(pos);
        if (world.spawnEntity(entityPON)) {
            return entityPON.createPicture(side);
        } else {
            return null;
        }
    }
    
    public static EntityPaintOnBlock getEntity(World world, BlockPos pos) {
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
        return StreamSupport.stream(list.getByClass(EntityPaintOnBlock.class).spliterator(), false)
                            .filter(e->pos.equals(e.getBlockPos()))
                            .findAny()
                            .orElse(null);
    }
    
    public static Picture getExistingPicture(World world, BlockPos pos, int side) {
        EntityPaintOnBlock entityPOB = getEntity(world, pos);
        if (entityPOB == null) {
            return null;
        }
        return entityPOB.getPicture(side);
    }
    
    public static boolean isValidBlockSide(World world, BlockPos pos, int side) {
        boolean valid;
        boolean air = false;
        boolean liquid = false;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        EnumFacing pside = EnumFacing.getFront(side);
        if (block.isAir(state, world, pos)) {
            valid = false;
            air = true;
        } else if (state.getMaterial().isLiquid()) {
            valid = false;
            liquid = true;
        } else if (state.isOpaqueCube()) {
            IBlockState neighborState = world.getBlockState(pos.offset(pside));
            valid = !neighborState.isOpaqueCube();
        } else if (state.getBlockFaceShape(world, pos, pside) == BlockFaceShape.SOLID) {
            valid = true;
        } else if (Stream.of(BlockStairs.class, BlockFence.class, BlockWall.class, BlockPane.class,
                             BlockFenceGate.class, BlockBrewingStand.class,
                             BlockBasePressurePlate.class, BlockButton.class, BlockCactus.class,
                             BlockSnow.class, BlockRedstoneWire.class, BlockVine.class)
                         .anyMatch(clazz->clazz.isAssignableFrom(block.getClass()))) {
            valid = true;
        } else if (Stream.of(Blocks.RAIL, Blocks.GOLDEN_RAIL,
                             Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL)
                         .anyMatch(block::equals)) {
            PropertyEnum<BlockRailBase.EnumRailDirection> shape;
            if (Stream.of(Blocks.GOLDEN_RAIL, Blocks.ACTIVATOR_RAIL).anyMatch(block::equals)) {
                shape = BlockRailPowered.SHAPE;
            } else if (block.equals(Blocks.DETECTOR_RAIL)) {
                shape = BlockRailDetector.SHAPE;
            } else {
                shape = BlockRail.SHAPE;
            }
            EnumRailDirection value = state.getValue(shape);
            valid = value != null && !value.isAscending();
        } else {
            AxisAlignedBB box = state.getBoundingBox(world, pos);
            AxisAlignedBB absoluteBox = box.offset(pos);
            List<AxisAlignedBB> list = new ArrayList<>();
            state.addCollisionBoxToList(world, pos, absoluteBox, list, null, false);
            valid = !list.isEmpty() && list.stream().allMatch(absoluteBox::equals);
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
