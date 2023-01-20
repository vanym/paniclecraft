package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.BiFunction;

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
    
    public static void savePainting(File file, Picture picture) throws IOException {
        savePainting(file, picture, 1, 1, (x, y)->picture);
    }
    
    public static void savePainting(
            File file,
            IPictureSize elementSize,
            int sizeX,
            int sizeY,
            BiFunction<Integer, Integer, Picture> getter) throws IOException {
        BufferedImage img = makePaintingImage(elementSize, sizeX, sizeY, getter);
        ImageIO.write(img, "png", file);
    }
    
    protected static BufferedImage makePaintingImage(
            IPictureSize elementSize,
            int sizeX,
            int sizeY,
            BiFunction<Integer, Integer, Picture> getter) {
        int stepX = elementSize.getWidth();
        int stepY = elementSize.getHeight();
        int totalSizeX = stepX * sizeX;
        int totalSizeY = stepY * sizeY;
        BufferedImage img = new BufferedImage(totalSizeX, totalSizeY, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < sizeY; ++y) {
            for (int x = 0; x < sizeX; ++x) {
                Picture picture = getter.apply(x, y);
                if (picture == null) {
                    continue;
                }
                int offsetX = x * stepX;
                int offsetY = y * stepY;
                int pxmax = Math.min(stepX, picture.getWidth());
                int pymax = Math.min(stepY, picture.getHeight());
                for (int py = 0; py < pymax; ++py) {
                    for (int px = 0; px < pxmax; ++px) {
                        Color color = picture.getPixelColor(px, py);
                        img.setRGB(offsetX + px, offsetY + py, color.getRGB());
                    }
                }
            }
        }
        return img;
    }
}
