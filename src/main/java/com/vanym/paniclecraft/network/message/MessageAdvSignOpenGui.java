package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.client.gui.GuiEditAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageAdvSignOpenGui {
    
    public final BlockPos pos;
    
    public MessageAdvSignOpenGui(BlockPos pos) {
        this.pos = pos;
    }
    
    public static void encode(MessageAdvSignOpenGui message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
    }
    
    public static MessageAdvSignOpenGui decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        return new MessageAdvSignOpenGui(pos);
    }
    
    public static void handleInWorld(MessageAdvSignOpenGui message, NetworkEvent.Context ctx) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT) {
            Minecraft minecraft = Minecraft.getInstance();
            TileEntity tile = minecraft.world.getTileEntity(message.pos);
            if (tile instanceof TileEntityAdvSign) {
                TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                Minecraft.getInstance().displayGuiScreen(new GuiEditAdvSign(tileAS));
            }
        }
    }
}
