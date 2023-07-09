package com.vanym.paniclecraft.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPaintingFrame extends BlockPaintingContainer {
    
    protected static final PropertyBool[] SIDES = Arrays.stream(EnumFacing.VALUES)
                                                        .map(EnumFacing::getName2)
                                                        .map(PropertyBool::create)
                                                        .toArray(PropertyBool[]::new);
    
    protected final double frameOutlineSize;
    
    protected final List<AxisAlignedBB> frameBoxes;
    
    public BlockPaintingFrame() {
        super(Material.WOOD);
        this.setRegistryName("paintingframe");
        this.setHardness(0.6F);
        this.frameOutlineSize = (1.0D / 16D) * 2.0D;
        this.frameBoxes = Collections.unmodifiableList(getFrameBoxes(this.frameOutlineSize));
        IBlockState state = this.blockState.getBaseState();
        for (PropertyBool side : SIDES) {
            state = state.withProperty(side, false);
        }
        this.setDefaultState(state);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPaintingFrame();
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SIDES);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
    
    @Override
    public boolean onBlockActivated(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityPlayer entityPlayer,
            EnumHand hand,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ) {
        if (!entityPlayer.isSneaking()) {
            return false;
        }
        TileEntityPaintingFrame tileP = (TileEntityPaintingFrame)world.getTileEntity(pos);
        if (tileP == null) {
            return false;
        }
        Picture picture = tileP.getPicture(side.getIndex());
        if (picture == null) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        if (entityPlayer != null) {
            rotatePicture(entityPlayer, picture, side, false);
        }
        Vec3d ePos = new Vec3d(pos).addVector(0.5, 0.5, 0.5)
                                   .add(new Vec3d(side.getDirectionVec()).scale(0.6D));
        ItemStack itemStack = ItemPainting.getPictureAsItem(picture);
        EntityItem entityItem = new EntityItem(world, ePos.x, ePos.y, ePos.z, itemStack);
        entityItem.setPickupDelay(3);
        world.spawnEntity(entityItem);
        tileP.clearPicture(side.getIndex());
        tileP.markForUpdate();
        world.notifyNeighborsOfStateChange(pos, this, true);
        return true;
    }
    
    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }
    
    @Override
    public boolean removedByPlayer(
            IBlockState state,
            World world,
            BlockPos pos,
            EntityPlayer player,
            boolean willHarvest) {
        if (player != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                int rot = getRotate(player, EnumFacing.UP, false);
                tilePF.rotateY(rot);
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame timePF = (TileEntityPaintingFrame)tile;
            ItemStack itemStack = ItemPaintingFrame.getFrameAsItem(timePF);
            spawnAsEntity(world, pos, itemStack);
        }
        super.breakBlock(world, pos, state);
    }
    
    @Override
    public void onBlockPlacedBy(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityLivingBase entity,
            ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, state, entity, itemStack);
        if (!itemStack.hasTagCompound()) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
            NBTTagCompound itemTag = itemStack.getTagCompound();
            for (int i = 0; i < ISidePictureProvider.N; i++) {
                final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(i);
                if (!itemTag.hasKey(TAG_PICTURE_I)) {
                    continue;
                }
                Picture picture = tilePF.createPicture(i);
                picture.readFromNBT(itemTag.getCompoundTag(TAG_PICTURE_I));
            }
            int rot = getRotate(entity, EnumFacing.UP, true);
            tilePF.rotateY(rot);
        }
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)world.getTileEntity(pos);
        return this.getActualState(state, tilePF);
    }
    
    public IBlockState getActualState(IBlockState state, TileEntityPaintingFrame tilePF) {
        for (int i = 0; i < SIDES.length; ++i) {
            state = state.withProperty(SIDES[i], tilePF.getPicture(i) != null);
        }
        return state;
    }
    
    public List<AxisAlignedBB> getFrameBoxes() {
        return this.frameBoxes;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isSideSolid(
            IBlockState state,
            IBlockAccess world,
            BlockPos pos,
            EnumFacing side) {
        TileEntityPaintingFrame tile = (TileEntityPaintingFrame)world.getTileEntity(pos);
        return tile.getPicture(side.getIndex()) != null;
    }
    
    @Override
    public BlockFaceShape getBlockFaceShape(
            IBlockAccess world,
            IBlockState state,
            BlockPos pos,
            EnumFacing face) {
        return this.isSideSolid(state, world, pos, face) ? BlockFaceShape.SOLID
                                                         : BlockFaceShape.BOWL;
    }
    
    @Override
    public void addCollisionBoxToList(
            IBlockState state,
            World world,
            BlockPos pos,
            AxisAlignedBB mask,
            List<AxisAlignedBB> list,
            @Nullable Entity entity,
            boolean isActualState) {
        if (!isActualState) {
            state = this.getActualState(state, world, pos);
        }
        Builder<AxisAlignedBB> facades = Stream.builder();
        for (int i = 0; i < 6; ++i) {
            if (state.getValue(SIDES[i])) {
                AxisAlignedBB box = GeometryUtils.getBoundsBySide(i, this.getPaintingOutlineSize());
                facades.add(box);
            }
        }
        List<AxisAlignedBB> boxes = this.getFrameBoxes();
        Stream.concat(boxes.stream(), facades.build()).forEach(box-> {
            AxisAlignedBB absoluteBox = box.offset(pos);
            if (mask.intersects(absoluteBox)) {
                list.add(absoluteBox);
            }
        });
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(
            IBlockState state,
            RayTraceResult target,
            World world,
            BlockPos pos,
            EntityPlayer player) {
        TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)world.getTileEntity(pos);
        return ItemPaintingFrame.getFrameAsItem(tilePF);
    }
    
    public static List<AxisAlignedBB> getFrameBoxes(final double frameWidth) {
        List<AxisAlignedBB> list = new ArrayList<>();
        for (int i = 0; i < 12; ++i) {
            int stage = i / 4;
            int i2d = i % 4;
            int i2dx = i2d / 2;
            int i2dy = i2d % 2;
            double minX2d = (i2dx == 1) ? 0.0D : (1.0D - frameWidth);
            double maxX2d = (i2dx == 1) ? frameWidth : 1.0D;
            double minY2d = (i2dy == 1) ? 0.0D : (1.0D - frameWidth);
            double maxY2d = (i2dy == 1) ? frameWidth : 1.0D;
            double minX = 0.0D, maxX = 1.0D, minY = 0.0D, maxY = 1.0D, minZ = 0.0D, maxZ = 1.0D;
            switch (stage) {
                case 0: {
                    minX = minX2d;
                    maxX = maxX2d;
                    minY = minY2d;
                    maxY = maxY2d;
                    minZ = frameWidth;
                    maxZ = 1.0D - frameWidth;
                }
                break;
                case 1: {
                    minX = minX2d;
                    maxX = maxX2d;
                    minZ = minY2d;
                    maxZ = maxY2d;
                }
                break;
                case 2: {
                    minX = frameWidth;
                    maxX = 1.0D - frameWidth;
                    minY = minX2d;
                    maxY = maxX2d;
                    minZ = minY2d;
                    maxZ = maxY2d;
                }
                break;
            }
            AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
            list.add(box);
        }
        return list;
    }
}
