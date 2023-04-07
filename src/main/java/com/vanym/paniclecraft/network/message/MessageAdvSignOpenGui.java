package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageAdvSignOpenGui
        implements
            IMessage,
            IMessageHandler<MessageAdvSignOpenGui, IMessage> {
    
    int x, y, z;
    
    public MessageAdvSignOpenGui() {}
    
    public MessageAdvSignOpenGui(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }
    
    public MessageAdvSignOpenGui(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageAdvSignOpenGui message, MessageContext ctx) {
        EntityPlayer player = FMLClientHandler.instance().getClient().player;
        player.openGui(Core.instance, GUIs.ADVSIGN.ordinal(), player.getEntityWorld(),
                       message.x, message.y, message.z);
        return null;
    }
}
