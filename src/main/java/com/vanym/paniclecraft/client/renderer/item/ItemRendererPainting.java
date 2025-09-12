package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.TextureHolder;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererPainting extends ItemStackTileEntityRenderer {
    
    public final TileEntityPaintingRenderer paintingTileRenderer;
    
    protected PictureTextureCache textureCache;
    
    public ItemRendererPainting(PictureTextureCache textureCache) {
        this.textureCache = textureCache;
        this.paintingTileRenderer = new TileEntityPaintingRenderer();
        this.paintingTileRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }
    
    @Override
    public void renderByItem(ItemStack item) {
        TileEntityPainting tilePainting = new TileEntityPainting();
        Picture picture = tilePainting.getPicture();
        CompoundNBT nbtPictureTag = ItemPainting.getPictureTag(item).orElse(null);
        INBT nbtImageTag = null;
        if (nbtPictureTag != null && !nbtPictureTag.isEmpty()) {
            nbtImageTag = nbtPictureTag.get(Picture.TAG_IMAGE);
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
    
    public static ItemRendererPainting create() {
        return new ItemRendererPainting(Core.instance.painting.textureCache);
    }
}
