package ee_man.mod3.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.tileEntity.TileEntityPlayerInv;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockPlayerInv extends BlockContainer{
	
	public BlockPlayerInv(int par1){
		super(par1, Material.glass);
		this.setHardness(.75F);
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(!par1World.isRemote){
			TileEntity tile = par1World.getBlockTileEntity(par2, par3, par4);
			if(tile instanceof TileEntityPlayerInv){
				TileEntityPlayerInv tilePI = (TileEntityPlayerInv)tile;
				tilePI.playerName = par5EntityPlayer.username;
				tilePI.player = par5EntityPlayer;
			}
		}
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2){
		return Block.beacon.getBlockTextureFromSide(par1);
	}
	
	public TileEntity createNewTileEntity(World par1World){
		return new TileEntityPlayerInv();
	}
}
