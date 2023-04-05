package com.vanym.paniclecraft.item;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemChessDesk extends ItemBlock {
    
    public static final String TAG_MOVES = TileEntityChessDesk.TAG_MOVES;
    
    public ItemChessDesk(Block block) {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }
    
    @Override
    @Nullable
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.HEAD;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack stack,
            @Nullable World world,
            List<String> list,
            ITooltipFlag flag) {
        if (!stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey(TAG_MOVES)) {
            NBTTagList movesTag = tag.getTagList(TAG_MOVES, 10);
            list.add(I18n.format("item.chess_desk.moves", movesTag.tagCount()));
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
                            String.format("item.chess_desk.player.%s.%s",
                                          side ? "white" : "black", many ? "many" : "one");
                    map.entrySet()
                       .stream()
                       .sorted(Comparator.comparing(Map.Entry<NBTTagCompound, Integer>::getValue)
                                         .reversed())
                       .map(e-> {
                           String name = e.getKey().getString(TileEntityChessDesk.TAG_PLAYERNAME);
                           return I18n.format(translate, many ? new Object[]{name, e.getValue()}
                                                              : new Object[]{name});
                       })
                       .forEach(list::add);
                });
            }
        }
    }
    
    public static ItemStack getSavedDesk(TileEntityChessDesk tileCD) {
        ItemStack stack = new ItemStack(Core.instance.deskgame.itemChessDesk);
        if (tileCD == null || tileCD.moves.isEmpty()) {
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
