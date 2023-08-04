package com.vanym.paniclecraft.network.message;

import java.io.IOException;

import com.vanym.paniclecraft.network.InWorldHandler;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAdvSignChange implements IMessage {
    
    NBTTagCompound tag;
    
    public MessageAdvSignChange() {}
    
    public MessageAdvSignChange(TileEntityAdvSign sign) {
        sign.writeToNBT(this.tag = new NBTTagCompound());
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
    
    public static class Handler extends InWorldHandler<MessageAdvSignChange> {
        
        @Override
        public void onMessageInWorld(MessageAdvSignChange message, MessageContext ctx) {
            if (message.tag == null) {
                return;
            }
            int x = message.tag.getInteger("x");
            int y = message.tag.getInteger("y");
            int z = message.tag.getInteger("z");
            if (!TileEntityAdvSign.isValidTag(message.tag)) {
                return;
            }
            EntityPlayerMP player = ctx.getServerHandler().player;
            TileEntity tile = player.world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof TileEntityAdvSign) {
                TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                if (tileAS.isEditor(player.getPersistentID())) {
                    tileAS.resetEditor();
                } else {
                    return;
                }
                tileAS.readFromNBT(message.tag);
                tileAS.markForUpdate();
            }
        }
    }
}
