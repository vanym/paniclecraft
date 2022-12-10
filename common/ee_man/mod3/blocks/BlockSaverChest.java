package ee_man.mod3.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.tileEntity.TileEntitySaverChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockSaverChest extends BlockContainer{
	
	public BlockSaverChest(int par1){
		super(par1, Material.wood);
		
		final float f = 0.0625F;
		this.setBlockBounds(0.0F + f, 0.0F, 0.0F + f, 1.0F - f, 1.0F - f * 2, 1.0F - f);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2){
		return Block.wood.getIcon(2, par2 / 4);
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		TileEntitySaverChest tile = (TileEntitySaverChest)par1World.getBlockTileEntity(par2, par3, par4);
		if(tile != null){
			if(!par1World.getBlockMaterial(par2, par3 + 1, par4).isSolid() || tile.open)
				tile.open = !tile.open;
			par1World.markBlockForUpdate(par2, par3, par4);
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity){
		TileEntitySaverChest tile = (TileEntitySaverChest)par1World.getBlockTileEntity(par2, par3, par4);
		final float f = 0.0625F;
		this.setBlockBounds(0.0F + f, 0.0F, 0.0F + f, f * 2, 1.0F - f * 2, 1.0F - f);
		super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		this.setBlockBounds(0.0F + f, 0.0F, 0.0F + f, 1.0F - f, 1.0F - f * 2, f);
		super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		this.setBlockBounds(1.0F - f * 2, 0.0F, 0.0F + f, 1.0F - f, 1.0F - f * 2, 1.0F - f);
		super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		this.setBlockBounds(0.0F + f, 0.0F, 1.0F - f * 2, 1.0F - f, 1.0F - f * 2, 1.0F - f);
		super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		this.setBlockBounds(0.0F + f, 0.0F, 0.0F + f, 1.0F - f, f * 2, 1.0F - f);
		super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		if(!(tile != null ? tile.open : false)){
			this.setBlockBounds(0.0F + f, 1.0F - f * 3, 0.0F + f, 1.0F - f, 1.0F - f * 2, 1.0F - f);
			super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		}
		this.setBlockBounds(0.0F + f, 0.0F, 0.0F + f, 1.0F - f, 1.0F - f * 2, 1.0F - f);
	}
	
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List){
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
		par3List.add(new ItemStack(par1, 1, 3));
	}
	
	public int damageDropped(int par1){
		return par1 / 4;
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World par1World){
		return new TileEntitySaverChest();
	}
	
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack){
		int l1 = MathHelper.floor_double((double)(par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int md = l1 + 4 * (par6ItemStack.getItemDamage() % 16);
		par1World.setBlockMetadataWithNotify(par2, par3, par4, md, 2);
	}
	
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5){
		if(par1World.getBlockMaterial(par2, par3 + 1, par4).isSolid()){
			TileEntitySaverChest tile = (TileEntitySaverChest)par1World.getBlockTileEntity(par2, par3, par4);
			if(tile != null)
				tile.open = false;
			par1World.markBlockForUpdate(par2, par3, par4);
		}
	}
}