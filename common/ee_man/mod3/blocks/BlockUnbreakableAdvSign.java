package ee_man.mod3.blocks;

import ee_man.mod3.tileEntity.TileEntityAdvSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockUnbreakableAdvSign extends BlockAdvSign{
	
	public BlockUnbreakableAdvSign(int par1, boolean par2, boolean par3){
		super(par1, par2, par3, 0);
		this.setHardness(-1.0F);
		this.setResistance(10000.0F);
	}
	
	public TileEntity createNewTileEntity(World par1World){
		return new TileEntityAdvSign(false);
	}
	
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5){
	}
}
