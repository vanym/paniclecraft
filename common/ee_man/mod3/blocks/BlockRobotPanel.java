package ee_man.mod3.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.tileEntity.TileEntityRobotPanel;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRobotPanel extends BlockContainer{
	
	private Class<? extends TileEntity> tileClass;
	
	public BlockRobotPanel(int par1, Class<? extends TileEntity> par2Class){
		super(par1, Material.rock);
		tileClass = par2Class;
		this.setHardness(3.0F);
		this.setResistance(2000.0F);
	}
	
	public TileEntity createNewTileEntity(World par1World){
		try{
			return tileClass.newInstance();
		} catch(Exception var3){
			throw new RuntimeException(var3);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName());
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(!par1World.isRemote){
			TileEntityRobotPanel tile = (TileEntityRobotPanel)par1World.getBlockTileEntity(par2, par3, par4);
			if(tile == null)
				return true;
			if(tile.ownerNick != null)
				par5EntityPlayer.openGui(Core.instance, 6, par1World, par2, par3, par4);
			else
				tile.ownerNick = par5EntityPlayer.username;
		}
		return true;
	}
	
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack){
		if(par5EntityLiving instanceof EntityPlayer){
			TileEntityRobotPanel tile = (TileEntityRobotPanel)par1World.getBlockTileEntity(par2, par3, par4);
			if(tile != null)
				tile.ownerNick = ((EntityPlayer)par5EntityLiving).username;
		}
	}
}
