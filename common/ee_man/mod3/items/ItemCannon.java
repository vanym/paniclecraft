package ee_man.mod3.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityCannon;

public class ItemCannon extends ItemMod3{
	
	public ItemCannon(int par1){
		super(par1);
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(par3World.getBlockId(par4, par5, par6) != Block.snow.blockID){
			if(par7 == 0){
				--par5;
			}
			
			if(par7 == 1){
				++par5;
			}
			
			if(par7 == 2){
				--par6;
			}
			
			if(par7 == 3){
				++par6;
			}
			
			if(par7 == 4){
				--par4;
			}
			
			if(par7 == 5){
				++par4;
			}
			
			if(!par3World.isAirBlock(par4, par5, par6)){
				return false;
			}
		}
		
		if(!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)){
			return false;
		}
		else{
			if(Core.blockCannonBlock.canPlaceBlockAt(par3World, par4, par5, par6) && par3World.canPlaceEntityOnSide(Core.blockCannonBlock.blockID, par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack)){
				--par1ItemStack.stackSize;
				par3World.setBlock(par4, par5, par6, Core.blockCannonBlock.blockID, 0, 3);
			}
			TileEntity tile = par3World.getBlockTileEntity(par4, par5, par6);
			if(tile instanceof TileEntityCannon){
				TileEntityCannon tileCannon = (TileEntityCannon)tile;
				int var11 = (int)par2EntityPlayer.rotationYaw;
				var11 -= 180;
				while(var11 < 0)
					var11 += 360;
				while(var11 >= 360)
					var11 -= 360;
				int var12 = (int)par2EntityPlayer.rotationPitch;
				if(var12 < 0)
					var12 = 0;
				tileCannon.direction = (short)var11;
				tileCannon.height = (byte)var12;
			}
			return true;
		}
	}
}