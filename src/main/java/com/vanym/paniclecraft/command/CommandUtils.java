package com.vanym.paniclecraft.command;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;

public class CommandUtils {
    
    protected static final SimpleCommandExceptionType REQUIRES_BLOCK_EXCEPTION_TYPE =
            new SimpleCommandExceptionType(
                    new TranslationTextComponent(
                            String.format("commands.%s.exception.noblock", DEF.MOD_ID)));
    
    public static String makeGiveCommand(String player, ItemStack stack) {
        return String.format("/give %s %s %d %d %s",
                             player, stack.getItem().getRegistryName(),
                             stack.getCount(), stack.getItemDamage(),
                             !stack.hasTagCompound() ? "" : stack.getTagCompound()
                                                                 .toString())
                     .trim();
    }
    
    public static HoverEvent makeItemHover(ItemStack stack) {
        return new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new TextComponentString(stack.writeToNBT(new NBTTagCompound()).toString()));
    }
    
    public static BlockRayTraceResult rayTraceBlocks(PlayerEntity player) throws CommandException {
        return rayTraceBlocks(player, 6.0D);
    }
    
    public static BlockRayTraceResult rayTraceBlocks(PlayerEntity player, double distance)
            throws CommandException {
        BlockRayTraceResult target = GeometryUtils.rayTraceBlocks(player, distance);
        if (target == null || target.getType() != RayTraceResult.Type.BLOCK) {
            throw REQUIRES_BLOCK_EXCEPTION_TYPE.create();
        }
        return target;
    }
    
    public static Function<WorldPictureProvider, WorldPicturePoint> makeProviderRayTraceMapper(
            EntityPlayer player) throws CommandException {
        RayTraceResult target = rayTraceBlocks(player);
        return (provider)->new WorldPicturePoint(
                provider,
                player.getEntityWorld(),
                target.getBlockPos(),
                target.sideHit.getIndex());
    }
    
    public static Picture rayTracePicture(
            EntityPlayer player,
            Stream<WorldPictureProvider> providers) throws CommandException {
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
