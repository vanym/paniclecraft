package com.vanym.paniclecraft.command;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;

public class CommandUtils {
    
    public static final SimpleCommandExceptionType REQUIRES_BLOCK_EXCEPTION_TYPE =
            new SimpleCommandExceptionType(
                    new TranslationTextComponent(
                            String.format("commands.%s.exception.noblock", DEF.MOD_ID)));
    
    public static final SimpleCommandExceptionType REQUIRES_PAINTING_EXCEPTION_TYPE =
            new SimpleCommandExceptionType(
                    new TranslationTextComponent(
                            String.format("commands.%s.exception.nopainting", DEF.MOD_ID)));
    
    public static String makeGiveCommand(String player, ItemStack stack) {
        return String.format("/give %s %s%s %d",
                             player, stack.getItem().getRegistryName(),
                             !stack.hasTag() ? "" : stack.getTag()
                                                         .toString(),
                             stack.getCount())
                     .trim();
    }
    
    public static HoverEvent makeItemHover(ItemStack stack) {
        return new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new StringTextComponent(stack.serializeNBT().toString()));
    }
    
    public static BlockRayTraceResult rayTraceBlocks(PlayerEntity player)
            throws CommandSyntaxException {
        return rayTraceBlocks(player, 6.0D);
    }
    
    public static BlockRayTraceResult rayTraceBlocks(PlayerEntity player, double distance)
            throws CommandSyntaxException {
        BlockRayTraceResult target = GeometryUtils.rayTraceBlocks(player, distance);
        if (target == null || target.getType() != RayTraceResult.Type.BLOCK) {
            throw REQUIRES_BLOCK_EXCEPTION_TYPE.create();
        }
        return target;
    }
    
    public static Function<WorldPictureProvider, WorldPicturePoint> makeProviderRayTraceMapper(
            PlayerEntity player) throws CommandSyntaxException {
        BlockRayTraceResult target = rayTraceBlocks(player);
        return (provider)->new WorldPicturePoint(
                provider,
                player.getEntityWorld(),
                target.getPos(),
                target.getFace().getIndex());
    }
    
    public static Picture rayTracePicture(
            PlayerEntity player,
            Stream<WorldPictureProvider> providers) throws CommandSyntaxException {
        try {
            return providers.map(makeProviderRayTraceMapper(player))
                            .map(p->p.getPicture())
                            .filter(p->p != null)
                            .findFirst()
                            .get();
        } catch (NoSuchElementException e) {
            throw REQUIRES_PAINTING_EXCEPTION_TYPE.create();
        }
    }
}
