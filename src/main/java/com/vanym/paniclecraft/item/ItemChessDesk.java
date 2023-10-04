package com.vanym.paniclecraft.item;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemChessDesk extends BlockItem {
    
    protected static final String TAG_MOVES = TileEntityChessDesk.TAG_MOVES;
    
    public ItemChessDesk(Block block) {
        super(block, Props.create().setTEISR(()->ItemRendererChessDesk::new));
        this.setRegistryName(block.getRegistryName());
    }
    
    @Override
    @Nullable
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
    
    @Override
    public int getBurnTime(ItemStack fuel) {
        if (getMoves(fuel).isPresent()) {
            return 0;
        }
        return -1;
    }
    
    @Override
    protected boolean onBlockPlaced(
            BlockPos pos,
            World worldIn,
            @Nullable PlayerEntity player,
            ItemStack stack,
            BlockState state) {
        return false; // skip
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
        Optional<ListNBT> movesTagOpt = getMoves(stack);
        if (movesTagOpt.isPresent()) {
            ListNBT movesTag = movesTagOpt.get();
            list.add(new TranslationTextComponent(
                    this.getTranslationKey() + ".moves",
                    movesTag.size()).applyTextStyle(TextFormatting.GRAY));
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
                       .peek(line->line.applyTextStyle(TextFormatting.GRAY))
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
        ListNBT list = new ListNBT();
        tileCD.writeMoves(list);
        if (list.isEmpty()) {
            return stack;
        }
        CompoundNBT tag = ItemUtils.getOrCreateBlockEntityTag(stack);
        tag.put(TAG_MOVES, list);
        return stack;
    }
    
    public static Optional<ListNBT> getMoves(ItemStack stack) {
        return ItemUtils.getBlockEntityTag(stack)
                        .filter(tag->tag.contains(TAG_MOVES, 9))
                        .map(tag->tag.getList(TAG_MOVES, 10));
    }
}
