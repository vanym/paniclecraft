package com.vanym.paniclecraft.core.component.painting;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

public class ImageUtils {
    
    protected static final ColorModel COLOR_MODEL_ALPHAFUL =
            new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB),
                    true,
                    false,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE);
    
    protected static final ColorModel COLOR_MODEL_ALPHALESS =
            new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB),
                    false,
                    false,
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE);
    
    protected static ColorModel getColorModel(boolean hasAlpha) {
        return hasAlpha ? COLOR_MODEL_ALPHAFUL : COLOR_MODEL_ALPHALESS;
    }
    
    public static boolean writePng(Image image, OutputStream out) {
        return writePng(image.getData(), image.getWidth(),
                        image.getHeight(), image.hasAlpha(),
                        out);
    }
    
    protected static boolean writePng(
            byte[] bytes,
            int width,
            int height,
            boolean hasAlpha,
            OutputStream out) {
        BufferedImage img = createRGBImage(bytes, width, height, hasAlpha);
        try {
            ImageIO.write(img, "png", out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    protected static DataBuffer wrapByteBuffer(ByteBuffer buffer) {
        return new DataBuffer(DataBuffer.TYPE_BYTE, buffer.limit()) {
            
            @Override
            public int getElem(int bank, int i) {
                return buffer.get(i);
            }
            
            @Override
            public void setElem(int bank, int i, int val) {
                buffer.put(i, (byte)val);
            }
        };
    }
    
    public static ByteBuffer readImageToDirectByteBuffer(InputStream in, boolean hasAlpha)
            throws IOException {
        return readImageToDirectByteBuffer(in, getColorModel(hasAlpha));
    }
    
    protected static ByteBuffer readImageToDirectByteBuffer(InputStream in, ColorModel colorModel)
            throws IOException {
        BufferedImage inImg = ImageIO.read(in);
        int size = inImg.getWidth() * inImg.getHeight() * colorModel.getNumComponents();
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        buffer.order(ByteOrder.nativeOrder());
        buffer.clear();
        DataBuffer dataBuf = wrapByteBuffer(buffer);
        SampleModel sampleModel =
                colorModel.createCompatibleSampleModel(inImg.getWidth(), inImg.getHeight());
        WritableRaster raster = new WritableRaster(sampleModel, dataBuf, new java.awt.Point()) {};
        BufferedImage myImg = new BufferedImage(colorModel, raster, false, null);
        ColorConvertOp colorConvert = new ColorConvertOp(null);
        colorConvert.filter(inImg, myImg);
        return buffer;
    }
    
    protected static BufferedImage createRGBImage(
            byte[] bytes,
            int width,
            int height,
            boolean hasAlpha) {
        ColorModel colorModel = getColorModel(hasAlpha);
        DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
        int[] bandOffsets = hasAlpha ? new int[]{0, 1, 2, 3} : new int[]{0, 1, 2};
        WritableRaster raster =
                Raster.createInterleavedRaster(buffer, width, height,
                                               width * colorModel.getNumComponents(),
                                               colorModel.getNumComponents(),
                                               bandOffsets, null);
        return new BufferedImage(colorModel, raster, false, null);
    }
    
    public static Image readImage(InputStream in, boolean hasAlpha) {
        try {
            BufferedImage inImg = ImageIO.read(in);
            Image raw = new Image(inImg.getWidth(), inImg.getHeight(), hasAlpha);
            BufferedImage myImg =
                    createRGBImage(raw.getData(), raw.getWidth(), raw.getHeight(), raw.hasAlpha());
            ColorConvertOp colorConvert = new ColorConvertOp(null);
            colorConvert.filter(inImg, myImg);
            return raw;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
