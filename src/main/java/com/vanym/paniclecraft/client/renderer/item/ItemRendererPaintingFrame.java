package com.vanym.paniclecraft.client.renderer.item;

import java.util.stream.IntStream;

import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.TextureHolder;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererPaintingFrame extends TileEntityItemStackRenderer {
    
    public final TileEntityPaintingFrameRenderer paintingFrameTileRenderer;
    
    protected PictureTextureCache textureCache;
    
    public ItemRendererPaintingFrame(PictureTextureCache textureCache) {
        this.textureCache = textureCache;
        this.paintingFrameTileRenderer = new TileEntityPaintingFrameRenderer();
        this.paintingFrameTileRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }
    
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        TileEntityPaintingFrame tilePF = new TileEntityPaintingFrame();
        NBTTagCompound[] tags = IntStream.range(0, ISidePictureProvider.N)
                                         .mapToObj(i->ItemPaintingFrame.getPictureTag(stack, i))
                                         .map(o->o.orElse(null))
                                         .toArray(NBTTagCompound[]::new);
        for (int i = 0; i < ISidePictureProvider.N; ++i) {
            if (tags[i] == null) {
                continue;
            }
            NBTTagCompound pictureTag = tags[i];
            NBTBase imageTag = pictureTag.getTag(Picture.TAG_IMAGE);
            Picture picture = tilePF.createPicture(i);
            TextureHolder obtainedTexture = this.textureCache.obtain(imageTag);
            if (obtainedTexture != null) {
                picture.setTexture(obtainedTexture);
                picture.imageChangeProcessed = true;
            } else if (pictureTag != null) {
                this.textureCache.put(imageTag, picture.getTexture());
                picture.deserializeNBT(pictureTag);
            }
        }
        this.paintingFrameTileRenderer.renderAtItem(tilePF);
    }
}
