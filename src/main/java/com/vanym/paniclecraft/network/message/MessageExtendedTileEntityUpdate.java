package com.vanym.paniclecraft.network.message;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class MessageExtendedTileEntityUpdate implements IMessage {
    
    protected int x;
    protected int y;
    protected int z;
    protected NBTTagCompound tag;
    
    public MessageExtendedTileEntityUpdate() {}
    
    public MessageExtendedTileEntityUpdate(int x, int y, int z, NBTTagCompound tag) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tag = tag;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readShort();
        this.z = buf.readInt();
        this.tag = new NBTTagCompound();
        try {
            DataInputStream input =
                    new DataInputStream(
                            new BufferedInputStream(
                                    new GZIPInputStream(
                                            new ByteBufInputStream(buf))));
            this.tag.func_152446_a(input, 0, new NBTSizeTracker(2097152L));
            input.close();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeShort(this.y);
        buf.writeInt(this.z);
        try {
            DataOutputStream output =
                    new DataOutputStream(
                            new GZIPOutputStream(
                                    new ByteBufOutputStream(buf)));
            this.tag.write(output);
            output.close();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }
    
    public static class Handler
            implements
                IMessageHandler<MessageExtendedTileEntityUpdate, IMessage> {
        @Override
        public IMessage onMessage(MessageExtendedTileEntityUpdate message, MessageContext ctx) {
            new S35PacketUpdateTileEntity(
                    message.x,
                    message.y,
                    message.z,
                    0,
                    message.tag).processPacket(ctx.getClientHandler());
            return null;
        }
    }
}
