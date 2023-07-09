package com.vanym.paniclecraft.item;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemChessDesk extends BlockItem {
    
    public static final String TAG_MOVES = TileEntityChessDesk.TAG_MOVES;
    
    public ItemChessDesk(Block block) {
        super(block, new Item.Properties().group(Core.instance.tab)
                                          .setTEISR(()->ItemRendererChessDesk::new));
        this.setRegistryName(block.getRegistryName());
    }
    
    @Override
    @Nullable
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
    
    @Override
    public int getBurnTime(ItemStack fuel) {
        if (fuel.hasTag()) {
            CompoundNBT itemTag = fuel.getTag();
            if (itemTag.contains(TAG_MOVES)) {
                return 0;
            }
        }
        return -1;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack stack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        if (!stack.hasTag()) {
            return;
        }
        CompoundNBT tag = stack.getTag();
        if (tag.contains(TAG_MOVES)) {
            ListNBT movesTag = tag.getList(TAG_MOVES, 10);
            list.add(new TranslationTextComponent(
                    this.getTranslationKey() + ".moves",
                    movesTag.size()));
            if (Screen.hasShiftDown()) {
                Map<CompoundNBT, Integer> white = new HashMap<>(), black = new HashMap<>();
                for (int i = 0; i < movesTag.size(); ++i) {
                    CompoundNBT moveTag = movesTag.getCompound(i);
                    if (!moveTag.contains(TileEntityChessDesk.TAG_PLAYER, 10)) {
                        continue;
                    }
                    CompoundNBT playerTag = moveTag.getCompound(TileEntityChessDesk.TAG_PLAYER);
                    Map<CompoundNBT, Integer> map = (i % 2 == 0) ? white : black;
                    map.put(playerTag, map.getOrDefault(playerTag, 0) + 1);
                }
                Stream.of(true, false).forEach(side-> {
                    Map<CompoundNBT, Integer> map = side ? white : black;
                    boolean many = map.size() > 1;
                    String translate =
                            String.format(this.getTranslationKey() + ".player.%s.%s",
                                          side ? "white" : "black", many ? "many" : "one");
                    map.entrySet()
                       .stream()
                       .sorted(Comparator.comparing(Map.Entry<CompoundNBT, Integer>::getValue)
                                         .reversed())
                       .map(e-> {
                           String name = e.getKey().getString(TileEntityChessDesk.TAG_PLAYERNAME);
                           return new TranslationTextComponent(
                                   translate,
                                   many ? new Object[]{name, e.getValue()}
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
        CompoundNBT tag = new CompoundNBT();
        ListNBT list = new ListNBT();
        tileCD.writeMovesToNBT(list);
        tag.put(TAG_MOVES, list);
        stack.setTag(tag);
        return stack;
    }
}
