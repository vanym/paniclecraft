package ee_man.mod3.core;

import java.util.Arrays;
import java.util.List;

import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityAdvSign;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class EventHandler{
	
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void playerInteractEvent(PlayerInteractEvent event){
		if(Core.blockAdvSignPost == null || Core.blockAdvSignWall == null)
			return;
		if(event.action == Action.RIGHT_CLICK_BLOCK && !event.isCanceled()){
			ItemStack is = event.entityPlayer.inventory.getCurrentItem();
			if(is != null)
				if(is.itemID == Item.book.itemID && Core.itemAdvSign != null && Core.blockAdvSignPost != null && Core.blockAdvSignWall != null && !event.entityPlayer.isSneaking()){
					TileEntity tEntity = event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z);
					if(tEntity instanceof TileEntitySign && event.entityPlayer.canPlayerEdit(event.x, event.y, event.z, event.face, is)){
						int block_id = event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z);
						int block_md = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
						TileEntitySign tEntitySign = (TileEntitySign)tEntity;
						String[] text = tEntitySign.signText;
						if(!event.entityPlayer.capabilities.isCreativeMode)
							--is.stackSize;
						event.entityPlayer.worldObj.setBlock(event.x, event.y, event.z, ((block_id == Block.signWall.blockID) ? Core.blockAdvSignWall.blockID : Core.blockAdvSignPost.blockID), block_md, 2);
						TileEntityAdvSign tEntityAdvSign = (TileEntityAdvSign)event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z);
						tEntityAdvSign.signText = text[0] + TileEntityAdvSign.separator + text[1] + TileEntityAdvSign.separator + text[2] + TileEntityAdvSign.separator + text[3];
						event.entityPlayer.swingItem();
					}
				}
		}
	}
}
