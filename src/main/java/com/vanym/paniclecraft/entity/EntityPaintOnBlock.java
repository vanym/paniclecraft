package com.vanym.paniclecraft.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

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
        this(Core.instance.painting.entityTypePaintOnBlock, world);
    }
    
    public EntityPaintOnBlock(EntityType<? extends EntityPaintOnBlock> type, World world) {
        super(type, world);
        this.noClip = true;
        this.setInvulnerable(true);
        
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
            this.remove();
            return true;
        }
        return false;
    }
    
    protected void needProceed() {
        this.proceeded = false;
    }
    
    @Override
    protected void registerData() {}
    
    protected void unloadPictures() {
        for (PictureHolder holder : this.holders) {
            if (holder != null) {
                holder.picture.unload();
            }
        }
    }
    
    @Override
    public void onRemovedFromWorld() {
        this.unloadPictures();
    }
    
    public void onWorldUnload() {
        this.unloadPictures();
    }
    
    @Override
    public void tick() {
        if (!this.world.isRemote && !this.proceeded) {
            this.clearEmpty();
            this.killIfEmpty();
            this.proceeded = true;
        }
    }
    
    @Override
    public void move(MoverType type, Vec3d pos) {}
    
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }
    
    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    
    @Override
    public PushReaction getPushReaction() {
        return PushReaction.IGNORE;
    }
    
    @Override
    public boolean isPushedByWater() {
        return false;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double dist) {
        return dist <= Core.instance.painting.clientConfig.renderPaintOnBlockMaxRenderDistanceSquared;
    }
    
    @Override
    protected void writeAdditional(CompoundNBT nbtTag) {
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (this.holders[i] != null) {
                CompoundNBT pictureTag = new CompoundNBT();
                this.holders[i].picture.writeToNBT(pictureTag);
                nbtTag.put(TAG_PICTURE_I, pictureTag);
            }
        }
    }
    
    @Override
    protected void readAdditional(CompoundNBT nbtTag) {
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (nbtTag.contains(TAG_PICTURE_I)) {
                PictureHolder holder = this.createHolder(i);
                holder.picture.readFromNBT(nbtTag.getCompound(TAG_PICTURE_I));
                holder.empty = false;
            } else {
                this.clearPicture(i);
            }
            this.picturesDataManager.setPictureDirty(i);
        }
    }
    
    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
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
                                 Direction.byIndex(this.side));
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
        @OnlyIn(Dist.CLIENT)
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
                if (stack.hasTag()) {
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
            super(id, DataSerializers.ITEMSTACK);
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
    
    public static EntityType<EntityPaintOnBlock> createType() {
        return EntityType.Builder.<EntityPaintOnBlock>create(EntityPaintOnBlock::new,
                                                             EntityClassification.MISC)
                                 .disableSummoning()
                                 .immuneToFire()
                                 .size(1.0F, 1.0F)
                                 .build(ID.toString());
    }
    
    public static int clearArea(World world, AxisAlignedBB box) {
        List<Entity> list = world.getEntitiesWithinAABB(EntityPaintOnBlock.class, box);
        list.forEach(e->e.remove());
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
        if (world.addEntity(entityPON)) {
            return entityPON.createPicture(side);
        } else {
            return null;
        }
    }
    
    public static EntityPaintOnBlock getEntity(World world, BlockPos pos) {
        if (!world.isAreaLoaded(pos, 0)) {
            return null;
        }
        Chunk chunk = world.getChunkAt(pos);
        ClassInheritanceMultiMap<Entity>[] lists = chunk.getEntityLists();
        int listIndex = pos.getY() / 16;
        if (listIndex < 0 || listIndex >= lists.length) {
            return null;
        }
        ClassInheritanceMultiMap<Entity> list = lists[listIndex];
        return list.func_219790_a(EntityPaintOnBlock.class)
                   .stream()
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
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Direction pside = Direction.byIndex(side);
        if (block.isAir(state, world, pos)) {
            valid = false;
            air = true;
        } else if (state.getMaterial().isLiquid()) {
            valid = false;
            liquid = true;
        } else if (state.isOpaqueCube(world, pos)) {
            BlockPos offpos = pos.offset(pside);
            BlockState neighborState = world.getBlockState(pos.offset(pside));
            valid = !neighborState.isOpaqueCube(world, offpos);
        } else if (Block.hasSolidSide(state, world, pos, pside)) {
            valid = true;
        } else if (Stream.of(StairsBlock.class, FenceBlock.class, WallBlock.class, PaneBlock.class,
                             FenceGateBlock.class, BrewingStandBlock.class,
                             AbstractPressurePlateBlock.class, AbstractButtonBlock.class,
                             CactusBlock.class, SnowBlock.class,
                             RedstoneWireBlock.class, VineBlock.class)
                         .anyMatch(clazz->clazz.isAssignableFrom(block.getClass()))) {
            valid = true;
        } else if (Stream.of(Blocks.RAIL, Blocks.POWERED_RAIL,
                             Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL)
                         .anyMatch(block::equals)) {
            EnumProperty<RailShape> shape;
            if (Stream.of(Blocks.POWERED_RAIL, Blocks.ACTIVATOR_RAIL).anyMatch(block::equals)) {
                shape = PoweredRailBlock.SHAPE;
            } else if (block.equals(Blocks.DETECTOR_RAIL)) {
                shape = DetectorRailBlock.SHAPE;
            } else {
                shape = RailBlock.SHAPE;
            }
            RailShape value = state.get(shape);
            valid = value != null && !value.isAscending();
        } else {
            VoxelShape shape = state.getShape(world, pos);
            valid = shape.toBoundingBoxList().size() == 1;
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
                BlockState state,
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
