package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererChessDesk implements IItemRenderer {
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (!Core.instance.deskgame.renderChessDeskItem) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean shouldUseRenderHelper(
            ItemRenderType type,
            ItemStack item,
            ItemRendererHelper helper) {
        return true;
    }
    
    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        TileEntityChessDesk tileChessDesk = new TileEntityChessDesk();
        tileChessDesk.blockMetadata = 0;
        switch (type) {
            case ENTITY: {
                GL11.glTranslatef(-0.25F, -0.2F, -0.25F);
                float scale = 0.55F;
                GL11.glScalef(scale, scale, scale);
            }
            break;
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.0F, 0.7F, -0.2F);
            break;
            case INVENTORY: {
                tileChessDesk.blockMetadata = 2;
                float scale = 1.07F;
                GL11.glScalef(scale, scale, scale);
            }
            break;
            default:
            break;
        }
        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            if (tag.hasKey(ItemChessDesk.TAG_MOVES, 9)) {
                NBTTagList list = tag.getTagList(ItemChessDesk.TAG_MOVES, 10);
                tileChessDesk.readMovesFromNBT(list);
            }
        }
        Core.instance.deskgame.tileChessDeskRenderer.renderTileEntityAt(tileChessDesk, 0, 0, 0, 0);
    }
}
