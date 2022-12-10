package ee_man.mod3.client.renderer.item;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.Core;
import ee_man.mod3.items.ItemPaintBrush;
import ee_man.mod3.tileEntity.TileEntityPainting;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeVersion;

@SideOnly(Side.CLIENT)
public class ItemRendererPainting implements IItemRenderer{
	
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
		TileEntityPainting tilePainting = new TileEntityPainting();
		tilePainting.blockType = Core.blockPainting;
		tilePainting.Row = 1;
		tilePainting.pic = new byte[]{(byte)ItemPaintBrush.DEFAULT_COLOR_RGB, (byte)ItemPaintBrush.DEFAULT_COLOR_RGB, (byte)ItemPaintBrush.DEFAULT_COLOR_RGB};
		tilePainting.blockMetadata = 3;
		if(item.hasTagCompound()){
			NBTTagCompound tag = item.getTagCompound();
			if(tag.hasKey("PaintingData")){
				NBTTagCompound tagData = tag.getCompoundTag("PaintingData");
				if(!tagData.hasNoTags())
					tilePainting.readFromNBT(tagData);
			}
		}
		if(type.equals(ItemRenderType.ENTITY)){
			GL11.glTranslatef(-0.25F, -0.18F, 0.0F);
			float var11 = 0.55F;
			GL11.glScalef(var11, var11, var11);
		}
		else
			if(type.equals(ItemRenderType.EQUIPPED) || (ForgeVersion.getBuildVersion() >= 687 ? type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON) : false)){
				tilePainting.blockMetadata = 4;
				GL11.glTranslatef(-0.6F, 0.6F, 0.0F);
			}
			else
				if(type.equals(ItemRenderType.INVENTORY)){
					float var12 = 1.2F;
					GL11.glScalef(var12, var12, var12);
					GL11.glTranslatef(0.0F, -0.18F, 0.42F);
				}
				else
					if(type.equals(ItemRenderType.FIRST_PERSON_MAP)){
						
					}
		TileEntityRenderer.instance.renderTileEntityAt(tilePainting, 0, 0, 0, 0);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
	
}
