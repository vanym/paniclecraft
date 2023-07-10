package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class ItemRendererPainting implements IItemRenderer {
    
    public final TileEntityPaintingRenderer paintingTileRenderer;
    
    protected PictureTextureCache textureCache;
    
    public ItemRendererPainting(PictureTextureCache textureCache) {
        this.textureCache = textureCache;
        this.paintingTileRenderer = new TileEntityPaintingRenderer();
        this.paintingTileRenderer.func_147497_a(TileEntityRendererDispatcher.instance);
    }
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (!Core.instance.painting.clientConfig.renderPaintingItem) {
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
        TileEntityPainting tilePainting = new TileEntityPainting();
        tilePainting.blockType = Core.instance.painting.blockPainting;
        tilePainting.blockMetadata = ForgeDirection.EAST.ordinal();
        Picture picture = tilePainting.getPicture(tilePainting.blockMetadata);
        NBTTagCompound nbtPictureTag = ItemPainting.getPictureTag(item).orElse(null);
        NBTBase nbtImageTag = null;
        if (nbtPictureTag != null && !nbtPictureTag.hasNoTags()) {
            nbtImageTag = nbtPictureTag.getTag(Picture.TAG_IMAGE);
        }
        int obtainedTexture = this.textureCache.obtainTexture(nbtImageTag);
        if (obtainedTexture >= 0) {
            picture.texture = obtainedTexture;
            picture.imageChangeProcessed = true;
        } else if (nbtPictureTag != null) {
            picture.readFromNBT(nbtPictureTag);
        }
        switch (type) {
            case ENTITY:
                float scaleent = 0.6F;
                GL11.glScalef(scaleent, scaleent, scaleent);
                GL11.glTranslatef(0.0F, -0.5F, -0.5F);
            break;
            case EQUIPPED:
                GL11.glRotatef(45.0F, 0.0F, -1.0F, 1.0F);
                GL11.glRotatef(30.0F, -1.0F, -0.7F, 1.0F);
                GL11.glTranslatef(1.0F, -0.5F, -0.5F);
            break;
            case EQUIPPED_FIRST_PERSON:
                tilePainting.blockMetadata = ForgeDirection.WEST.ordinal();
                GL11.glTranslatef(-0.6F, 0.6F, 0.0F);
            break;
            case INVENTORY:
                tilePainting.blockMetadata = ForgeDirection.SOUTH.ordinal();
                float scaleinv = 1.2F;
                GL11.glScalef(scaleinv, scaleinv, scaleinv);
                GL11.glTranslatef(0.0F, -0.12F, 0.42F);
            break;
            case FIRST_PERSON_MAP:
            default:
            break;
        }
        this.paintingTileRenderer.renderTileEntityAtItem(tilePainting);
        if (obtainedTexture < 0) {
            this.textureCache.putTexture(nbtImageTag, picture.texture);
        }
    }
    
}
