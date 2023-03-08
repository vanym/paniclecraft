package com.vanym.paniclecraft.item;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemChessDesk extends ItemBlock {
    
    public static final String TAG_MOVES = TileEntityChessDesk.TAG_MOVES;
    
    public ItemChessDesk(Block block) {
        super(block);
    }
    
    @Override
    public boolean placeBlockAt(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ,
            int metadata) {
        int rot = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, rot);
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack stack,
            EntityPlayer player,
            List list,
            boolean advancedItemTooltips) {
        if (!stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey(TAG_MOVES)) {
            NBTTagList movesTag = tag.getTagList(TAG_MOVES, 10);
            list.add(StatCollector.translateToLocalFormatted("item.chessDesk.moves",
                                                             movesTag.tagCount()));
            if (GuiScreen.isShiftKeyDown()) {
                Map<NBTTagCompound, Integer> white = new HashMap<>(), black = new HashMap<>();
                for (int i = 0; i < movesTag.tagCount(); ++i) {
                    NBTTagCompound moveTag = movesTag.getCompoundTagAt(i);
                    if (!moveTag.hasKey(TileEntityChessDesk.TAG_PLAYER, 10)) {
                        continue;
                    }
                    NBTTagCompound playerTag =
                            moveTag.getCompoundTag(TileEntityChessDesk.TAG_PLAYER);
                    Map<NBTTagCompound, Integer> map = (i % 2 == 0) ? white : black;
                    map.put(playerTag, map.getOrDefault(playerTag, 0) + 1);
                }
                Stream.of(true, false).forEach(side-> {
                    Map<NBTTagCompound, Integer> map = side ? white : black;
                    boolean many = map.size() > 1;
                    String translate =
                            String.format("item.chessDesk.player.%s.%s",
                                          side ? "white" : "black", many ? "many" : "one");
                    map.entrySet()
                       .stream()
                       .sorted(Comparator.comparing(Map.Entry<NBTTagCompound, Integer>::getValue)
                                         .reversed())
                       .map(e-> {
                           String name = e.getKey().getString(TileEntityChessDesk.TAG_PLAYERNAME);
                           return StatCollector.translateToLocalFormatted(translate,
                                                                          many ? new Object[]{name,
                                                                                              e.getValue()}
                                                                               : new Object[]{name});
                       })
                       .forEach(list::add);
                });
            }
        }
    }
    
    public static ItemStack getSavedDesk(TileEntityChessDesk tileCD) {
        ItemStack stack = new ItemStack(Core.instance.deskgame.itemChessDesk);
        if (tileCD == null) {
            return stack;
        }
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        tileCD.writeMovesToNBT(list);
        tag.setTag(TAG_MOVES, list);
        stack.setTagCompound(tag);
        return stack;
    }
}
