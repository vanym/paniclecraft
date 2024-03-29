package com.vanym.paniclecraft.block;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.tileentity.TileEntityCannon;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockCannon extends ContainerBlock {
    
    protected static final VoxelShape CANNON_SHAPE =
            VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, 1.0D / 16.0D, 1.0D);
    
    public BlockCannon() {
        super(Block.Properties.create(Material.ANVIL)
                              .sound(SoundType.STONE)
                              .hardnessAndResistance(1.5F));
        this.setRegistryName("cannon");
    }
    
    @Override
    public boolean onBlockActivated(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockRayTraceResult hit) {
        if (!world.isRemote) {
            INamedContainerProvider container = this.getContainer(state, world, pos);
            if (container != null) {
                NetworkHooks.openGui((ServerPlayerEntity)player, container, pos);
            }
        }
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityCannon();
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            IBlockReader world,
            BlockPos pos,
            ISelectionContext context) {
        return CANNON_SHAPE;
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }
    
    @Override
    public void onBlockPlacedBy(
            World world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity entity,
            ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);
        TileEntity tile = world.getTileEntity(pos);
        if (entity != null && tile instanceof TileEntityCannon) {
            TileEntityCannon tileCannon = (TileEntityCannon)tile;
            double direction = Math.round(180.0D + entity.rotationYaw);
            double height = Math.round(entity.rotationPitch);
            SideUtils.runSync(!world.isRemote, tileCannon, ()-> {
                tileCannon.setDirection(direction);
                tileCannon.setHeight(Math.max(0.0D, Math.min(90.0D, height)));
            });
        }
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(
            BlockState state,
            World worldIn,
            BlockPos pos,
            BlockState newState,
            boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof IInventory) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
            
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }
}
