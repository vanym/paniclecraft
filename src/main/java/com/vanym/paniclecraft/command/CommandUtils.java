package com.vanym.paniclecraft.command;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.MainUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class CommandUtils {
    
    public static String makeGiveCommand(String player, ItemStack stack) {
        return String.format("/give %s %s %d %d %s",
                             player, Item.itemRegistry.getNameForObject(stack.getItem()),
                             stack.stackSize, stack.getItemDamage(),
                             !stack.hasTagCompound() ? "" : stack.getTagCompound()
                                                                 .toString())
                     .trim();
    }
    
    public static HoverEvent makeItemHover(ItemStack stack) {
        return new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new ChatComponentText(stack.writeToNBT(new NBTTagCompound()).toString()));
    }
    
    public static EntityPlayerMP getSenderAsPlayer(ICommandSender sender) {
        if (sender instanceof EntityPlayerMP) {
            return (EntityPlayerMP)sender;
        } else {
            throw new CommandException(
                    String.format("commands.%s.exception.playerless", DEF.MOD_ID));
        }
    }
    
    public static MovingObjectPosition rayTraceBlocks(EntityPlayer player) {
        return rayTraceBlocks(player, 6.0D);
    }
    
    public static MovingObjectPosition rayTraceBlocks(EntityPlayer player, double distance) {
        MovingObjectPosition target = MainUtils.rayTraceBlocks(player, distance);
        if (target == null || target.typeOfHit != MovingObjectType.BLOCK) {
            throw new CommandException(String.format("commands.%s.exception.noblock", DEF.MOD_ID));
        }
        return target;
    }
    
    public static Function<WorldPictureProvider, WorldPicturePoint> makeProviderRayTraceMapper(
            EntityPlayer player) {
        MovingObjectPosition target = rayTraceBlocks(player);
        return (provider)->new WorldPicturePoint(
                provider,
                player.getEntityWorld(),
                target.blockX,
                target.blockY,
                target.blockZ,
                target.sideHit);
    }
    
    public static Picture rayTracePicture(
            EntityPlayer player,
            Stream<WorldPictureProvider> providers) {
        try {
            return providers.map(makeProviderRayTraceMapper(player))
                            .map(p->p.getPicture())
                            .filter(p->p != null)
                            .findFirst()
                            .get();
        } catch (NoSuchElementException e) {
            throw new NopaintingException();
        }
    }
    
    @SuppressWarnings("serial")
    public static class NopaintingException extends CommandException {
        
        public NopaintingException() {
            super(String.format("commands.%s.exception.nopainting", DEF.MOD_ID));
        }
        
    }
}
