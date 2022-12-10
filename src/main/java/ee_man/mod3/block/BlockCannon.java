package ee_man.mod3.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.core.GUIs;
import ee_man.mod3.init.ModItems;
import ee_man.mod3.tileentity.TileEntityCannon;

public class BlockCannon extends BlockContainerMod3{
	
	public BlockCannon(){
		super(Material.anvil);
		this.setBlockName("cannon");
		this.setHardness(1.5F);
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		par5EntityPlayer.openGui(Core.instance, GUIs.CANNON.ordinal(), par1World, par2, par3, par4);
		return true;
	}
	
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_){
		return ModItems.itemCannon;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int md){
		return new TileEntityCannon();
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		return null;// TODO
		// return AxisAlignedBB.getBoundingBox((double)par2 + 0.0F, (double)par3
		// + 0.0F, (double)par4 + 0.0F, (double)par2 + 1.0F, (double)par3 +
		// 0.0625F, (double)par4 + 1.0F);
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side){
		return ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)) == side.getOpposite();
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2){
		return Blocks.anvil.getBlockTextureFromSide(par1);
	}
}
