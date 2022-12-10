package ee_man.mod3.blocks;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.items.utils.IUpgradeForPrivateChest;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;
import ee_man.mod3.utils.Localization;
import ee_man.mod3.utils.MainUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPrivateChest extends BlockContainer{
	
	public BlockPrivateChest(int par1){
		super(par1, Material.rock);
		this.setHardness(10.0F);
		this.setResistance(2000.0F);
		this.setStepSound(soundStoneFootstep);
		
		final float f = 0.0625F;
		this.setBlockBounds(0.0F + f, 0.0F, 0.0F + f, 1.0F - f, 1.0F - f * 2, 1.0F - f);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2){
		return Block.obsidian.getIcon(par1, par2);
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(!par1World.isRemote){
			TileEntity tile = par1World.getBlockTileEntity(par2, par3, par4);
			if(tile instanceof TileEntityPrivateChest){
				TileEntityPrivateChest tilePC = (TileEntityPrivateChest)tile;
				if(tilePC.canPlayerOpen(par5EntityPlayer, par6, par7, par8, par9))
					par5EntityPlayer.openGui(Core.instance, 3, par1World, par2, par3, par4);
				else
					if(MainUtils.isPlayerOp(par5EntityPlayer.username)){
						par5EntityPlayer.addChatMessage(Localization.get("text.opOpenChest"));
						par5EntityPlayer.openGui(Core.instance, 3, par1World, par2, par3, par4);
					}
			}
		}
		return true;
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6){
		TileEntity tile = par1World.getBlockTileEntity(par2, par3, par4);
		if(tile instanceof TileEntityPrivateChest && tile != null){
			TileEntityPrivateChest tilePC = (TileEntityPrivateChest)tile;
			if(!tilePC.canBeBroken()){
				par1World.setBlock(par2, par3, par4, this.blockID, tilePC.getBlockMetadata(), 2);
				return;
			}
			for(int i = 0; i < tilePC.inventoryUpgrades.getSizeInventory(); i++){
				ItemStack is = tilePC.inventoryUpgrades.getStackInSlot(i);
				if(is != null)
					((IUpgradeForPrivateChest)is.getItem()).onChestBreak(tilePC, is);
			}
			for(int i = 0; i < tilePC.inventoryItems.getSizeInventory(); i++){
				ItemStack is = tilePC.inventoryItems.getStackInSlot(i);
				if(is != null)
					this.dropBlockAsItem_do(par1World, par2, par3, par4, is);
			}
			for(int i = 0; i < tilePC.inventoryUpgrades.getSizeInventory(); i++){
				ItemStack is = tilePC.inventoryUpgrades.getStackInSlot(i);
				if(is != null)
					this.dropBlockAsItem_do(par1World, par2, par3, par4, is);
				
			}
		}
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	public float getBlockHardness(World par1World, int par2, int par3, int par4){
		// if(!par1World.isRemote){
		TileEntity tile = par1World.getBlockTileEntity(par2, par3, par4);
		if(tile instanceof TileEntityPrivateChest && tile != null){
			TileEntityPrivateChest tilePC = (TileEntityPrivateChest)tile;
			if(!tilePC.canBeBroken()){
				return -1.0F;
			}
		}
		// }
		return this.blockHardness;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world){
		return new TileEntityPrivateChest();
	}
	
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack){
		int l1 = MathHelper.floor_double((double)(par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int md = l1 + 4 * (par6ItemStack.getItemDamage() % 16);
		par1World.setBlockMetadataWithNotify(par2, par3, par4, md, 2);
	}
	
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile == null || (tile instanceof TileEntityPrivateChest && ((TileEntityPrivateChest)tile).canBeBroken()))
			list.add(new ItemStack(this));
		return list;
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
	
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z){
		return false;
	}
}
