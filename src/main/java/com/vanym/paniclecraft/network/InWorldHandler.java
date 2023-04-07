package com.vanym.paniclecraft.network;

import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class InWorldHandler<
        REQ extends IMessage> implements IMessageHandler<REQ, IMessage> {
    
    @Override
    public final IMessage onMessage(REQ message, MessageContext ctx) {
        IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
        if (thread.isCallingFromMinecraftThread()) {
            this.onMessageInWorld(message, ctx);
        } else {
            thread.addScheduledTask(()->this.onMessageInWorld(message, ctx));
        }
        return null;
    }
    
    protected abstract void onMessageInWorld(REQ message, MessageContext ctx);
}
