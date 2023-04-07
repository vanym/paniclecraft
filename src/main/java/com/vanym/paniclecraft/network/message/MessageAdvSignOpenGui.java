package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageAdvSignOpenGui implements IMessage {
    
    int x, y, z;
    
    public MessageAdvSignOpenGui() {}
    
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
    
    public static class Handler implements IMessageHandler<MessageAdvSignOpenGui, IMessage> {
        
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageAdvSignOpenGui message, MessageContext ctx) {
            EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
            player.openGui(Core.instance, GUIs.ADVSIGN.ordinal(), player.getEntityWorld(),
                           message.x, message.y, message.z);
            return null;
        }
    }
}
