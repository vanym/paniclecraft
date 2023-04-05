package com.vanym.paniclecraft.network.message;

import java.io.IOException;

import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAdvSignChange
        implements
            IMessage,
            IMessageHandler<MessageAdvSignChange, IMessage> {
    
    NBTTagCompound tag;
    
    public MessageAdvSignChange() {}
    
    public MessageAdvSignChange(TileEntityAdvSign tileAS) {
        tileAS.writeToNBT(this.tag = new NBTTagCompound());
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        try {
            this.tag = pb.readCompoundTag();
        } catch (IOException e) {
            this.tag = null;
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        try {
            pb.writeCompoundTag(this.tag);
        } catch (EncoderException e) {
            pb.writeShort(-1);
        }
    }
    
    @Override
    public IMessage onMessage(MessageAdvSignChange message, MessageContext ctx) {
        if (message.tag == null) {
            return null;
        }
        int x = message.tag.getInteger("x");
        int y = message.tag.getInteger("y");
        int z = message.tag.getInteger("z");
        NBTTagList linesTag = message.tag.getTagList(TileEntityAdvSign.TAG_LINES, 8);
        int size = linesTag.tagCount();
        if (size > TileEntityAdvSign.MAX_LINES || size < TileEntityAdvSign.MIN_LINES) {
            return null;
        }
        for (int i = 0; i < size; ++i) {
            String line = linesTag.getStringTagAt(i);
            if (line.length() > 64 * size) {
                return null;
            }
        }
        TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(new BlockPos(x, y, z));
        if (tile != null && tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            if (tileAS.isEditor(ctx.getServerHandler().player)) {
                tileAS.resetEditor();
            } else {
                return null;
            }
            tileAS.readFromNBT(message.tag);
            tileAS.markForUpdate();
        }
        return null;
    }
}
