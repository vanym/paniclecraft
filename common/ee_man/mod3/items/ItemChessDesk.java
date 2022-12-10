package ee_man.mod3.items;

import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityChessDesk;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemChessDesk extends ItemMod3{
	
	public ItemChessDesk(int par1){
		super(par1);
		this.setMaxStackSize(1);
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
			if(Core.blockChessDesk.canPlaceBlockAt(par3World, par4, par5, par6)){
				--par1ItemStack.stackSize;
				int var11 = MathHelper.floor_double((double)(par2EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
				par3World.setBlock(par4, par5, par6, Core.blockChessDesk.blockID, var11, 3);
				TileEntityChessDesk tile = (TileEntityChessDesk)par3World.getBlockTileEntity(par4, par5, par6);
				NBTTagCompound var1 = par1ItemStack.getTagCompound();
				if(tile != null && var1 != null){
					if(var1.hasKey("desk"))
						tile.desk.desk = var1.getByteArray("desk");
					if(var1.hasKey("lastFrom"))
						tile.desk.lastFrom = var1.getByte("lastFrom");
					if(var1.hasKey("lastTo"))
						tile.desk.lastTo = var1.getByte("lastTo");
					if(var1.hasKey("isWhiteTurn"))
						tile.desk.isWhiteTurn = var1.getBoolean("isWhiteTurn");
					if(var1.hasKey("whitePlayer"))
						tile.whitePlayer = var1.getString("whitePlayer");
					if(var1.hasKey("blackPlayer"))
						tile.blackPlayer = var1.getString("blackPlayer");
					if(var1.hasKey("display")){
						NBTTagCompound var2 = var1.getCompoundTag("display");
						if(var2.hasKey("Name"))
							tile.name = var2.getString("Name");
					}
				}
			}
			
			return true;
		}
	}
}
