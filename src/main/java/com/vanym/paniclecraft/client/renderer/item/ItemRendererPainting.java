package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.TextureHolder;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererPainting extends TileEntityItemStackRenderer {
    
    public final TileEntityPaintingRenderer paintingTileRenderer;
    
    protected PictureTextureCache textureCache;
    
    public ItemRendererPainting(PictureTextureCache textureCache) {
        this.textureCache = textureCache;
        this.paintingTileRenderer = new TileEntityPaintingRenderer();
        this.paintingTileRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }
    
    @Override
    public void renderByItem(ItemStack item, float partialTicks) {
        TileEntityPainting tilePainting = new TileEntityPainting();
        Picture picture = tilePainting.getPicture();
        NBTTagCompound nbtPictureTag = ItemPainting.getPictureTag(item).orElse(null);
        NBTBase nbtImageTag = null;
        if (nbtPictureTag != null && !nbtPictureTag.hasNoTags()) {
            nbtImageTag = nbtPictureTag.getTag(Picture.TAG_IMAGE);
        }
        TextureHolder obtainedTexture = this.textureCache.obtain(nbtImageTag);
        if (obtainedTexture != null) {
            picture.setTexture(obtainedTexture);
            picture.imageChangeProcessed = true;
        } else if (nbtPictureTag != null) {
            this.textureCache.put(nbtImageTag, picture.getTexture());
            picture.deserializeNBT(nbtPictureTag);
        }
        this.paintingTileRenderer.renderAtItem(tilePainting);
    }
}
