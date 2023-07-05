package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.container.ContainerPaintingViewBase;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessagePaintingViewAddPicture {
    
    protected final int x, y;
    protected final ItemStack stack;
    
    public MessagePaintingViewAddPicture(int x, int y, ItemStack stack) {
        this.x = x;
        this.y = y;
        this.stack = stack;
    }
    
    public static void encode(MessagePaintingViewAddPicture message, PacketBuffer buf) {
        buf.writeInt(message.x);
        buf.writeInt(message.y);
        buf.writeItemStack(message.stack);
    }
    
    public static MessagePaintingViewAddPicture decode(PacketBuffer buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        ItemStack stack = buf.readItemStack();
        return new MessagePaintingViewAddPicture(x, y, stack);
    }
    
    public static void handleInWorld(
            MessagePaintingViewAddPicture message,
            NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (!ContainerPaintingViewBase.class.isInstance(player.openContainer)) {
            return;
        }
        ContainerPaintingViewBase view = (ContainerPaintingViewBase)player.openContainer;
        if (!view.editable) {
            return;
        }
        Picture picture = new Picture(true);
        if (message.stack == null || message.stack.isEmpty()
            || !ItemPainting.fillPicture(picture, message.stack)) {
            return;
        }
        view.addPicture(message.x, message.y, picture);
        picture.unload();
    }
}
