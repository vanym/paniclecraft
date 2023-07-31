package com.vanym.paniclecraft.network.message;

import java.io.IOException;
import java.util.stream.Stream;

import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

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
            this.tag = pb.readNBTTagCompoundFromBuffer();
        } catch (IOException e) {
            this.tag = null;
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        try {
            pb.writeNBTTagCompoundToBuffer(this.tag);
        } catch (IOException e) {
            pb.writeShort(-1);
        }
    }
    
    public static class Handler implements IMessageHandler<MessageAdvSignChange, IMessage> {
        
        @Override
        public IMessage onMessage(MessageAdvSignChange message, MessageContext ctx) {
            if (message.tag == null) {
                return null;
            }
            int x = message.tag.getInteger("x");
            int y = message.tag.getInteger("y");
            int z = message.tag.getInteger("z");
            if (!Stream.of(TileEntityAdvSign.TAG_FRONTTEXT,
                           TileEntityAdvSign.TAG_BACKTEXT)
                       .map(message.tag::getCompoundTag)
                       .map(AdvSignText::new)
                       .allMatch(AdvSignText::isValid)) {
                return null;
            }
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            TileEntity tile = player.worldObj.getTileEntity(x, y, z);
            if (tile instanceof TileEntityAdvSign) {
                TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                if (tileAS.isEditor(player)) {
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
}
