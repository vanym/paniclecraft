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
    protected static final ColorModel COLOR_MODEL =
            new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB),
                    new int[]{8, 8, 8},
                    false,
                    false,
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE);
    
    public static boolean writePng(Image image, OutputStream out) {
        return writePng(image.getData(), image.getWidth(), image.getHeight(), out);
    }
    
    protected static boolean writePng(byte[] bytes, int width, int height, OutputStream out) {
        BufferedImage img = createRGBImage(bytes, width, height);
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
    
    public static ByteBuffer readImageToDirectByteBuffer(InputStream in) throws IOException {
        BufferedImage inImg = ImageIO.read(in);
        int size = inImg.getWidth() * inImg.getHeight() * COLOR_MODEL.getNumComponents();
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        buffer.order(ByteOrder.nativeOrder());
        buffer.clear();
        DataBuffer dataBuf = wrapByteBuffer(buffer);
        SampleModel sampleModel =
                COLOR_MODEL.createCompatibleSampleModel(inImg.getWidth(), inImg.getHeight());
        WritableRaster raster = new WritableRaster(sampleModel, dataBuf, new java.awt.Point()) {};
        BufferedImage myImg = new BufferedImage(COLOR_MODEL, raster, false, null);
        ColorConvertOp colorConvert = new ColorConvertOp(null);
        colorConvert.filter(inImg, myImg);
        return buffer;
    }
    
    protected static BufferedImage createRGBImage(byte[] bytes, int width, int height) {
        DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
        WritableRaster raster =
                Raster.createInterleavedRaster(buffer, width, height, width * 3, 3,
                                               new int[]{0, 1, 2}, null);
        return new BufferedImage(COLOR_MODEL, raster, false, null);
    }
    
    public static Image readImage(InputStream in) {
        try {
            BufferedImage inImg = ImageIO.read(in);
            Image raw = new Image(inImg.getWidth(), inImg.getHeight());
            BufferedImage myImg =
                    createRGBImage(raw.getData(), raw.getWidth(), raw.getHeight());
            ColorConvertOp colorConvert = new ColorConvertOp(null);
            colorConvert.filter(inImg, myImg);
            return raw;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
