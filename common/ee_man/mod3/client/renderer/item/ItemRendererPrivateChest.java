package ee_man.mod3.client.renderer.item;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.tileEntity.TileEntityPrivateChest;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererPrivateChest implements IItemRenderer{
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type){
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){
		return true;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data){
		TileEntityPrivateChest tilePrivateChest = new TileEntityPrivateChest();
		tilePrivateChest.blockMetadata = 0;
		switch(type){
			case ENTITY:
				tilePrivateChest.blockMetadata += 1;
				GL11.glTranslatef(-0.5F, -0.45F, -0.5F);
			break;
			case EQUIPPED:
			break;
			case FIRST_PERSON_MAP:
			break;
			case INVENTORY:
				tilePrivateChest.blockMetadata += 2;
			break;
			default:
			break;
		}
		
		TileEntityRenderer.instance.renderTileEntityAt(tilePrivateChest, 0, 0, 0, 0);
	}
	
}
