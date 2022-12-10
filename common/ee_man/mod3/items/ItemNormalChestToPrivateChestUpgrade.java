package ee_man.mod3.items;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class ItemNormalChestToPrivateChestUpgrade extends ItemMod3{
	
	private int blockID;
	
	public ItemNormalChestToPrivateChestUpgrade(int par1, int par2BlockID){
		super(par1);
		blockID = par2BlockID;
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote){
			TileEntity tile = par3World.getBlockTileEntity(par4, par5, par6);
			if(tile != null && tile instanceof TileEntityChest){
				TileEntityChest tileC = (TileEntityChest)tile;
				if(tileC.numUsingPlayers <= 0){
					ItemStack[] chestContents = (ItemStack[])ObfuscationReflectionHelper.getPrivateValue(TileEntityChest.class, tileC, 0);
					int md = par3World.getBlockMetadata(par4, par5, par6);
					TileEntityPrivateChest tilePC = new TileEntityPrivateChest();
					System.arraycopy(chestContents, 0, tilePC.inventoryItems.items, 0, Math.min(chestContents.length, tilePC.inventoryItems.items.length));
					for(int i = 0; i < Math.min(chestContents.length, tilePC.inventoryItems.items.length); i++)
						chestContents[i] = null;
					par3World.setBlock(par4, par5, par6, blockID, changeMetadata(md), 3);
					par3World.setBlockTileEntity(par4, par5, par6, tilePC);
					if(!par2EntityPlayer.capabilities.isCreativeMode)
						par1ItemStack.stackSize--;
				}
			}
		}
		return true;
	}
	
	public static int changeMetadata(int input){
		switch(input){
			case 2:
				return 0;
			case 5:
				return 1;
			case 3:
				return 2;
			case 4:
				return 3;
			default:
				return 0;
		}
	}
}
