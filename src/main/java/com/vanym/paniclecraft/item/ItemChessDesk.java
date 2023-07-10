package com.vanym.paniclecraft.item;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.ItemUtils;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemChessDesk extends ItemBlockMod3 {
    
    protected static final String TAG_MOVES = TileEntityChessDesk.TAG_MOVES;
    
    public ItemChessDesk(Block block) {
        super(block);
    }
    
    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        if (armorType == 0) {
            return true;
        }
        return super.isValidArmor(stack, armorType, entity);
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
    
    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void onFuelBurnTime(net.minecraftforge.event.FuelBurnTimeEvent event) {
        ItemStack fuel = event.fuel;
        if (fuel.getItem() instanceof ItemChessDesk && getMoves(fuel).isPresent()) {
            event.burnTime = 0;
            event.setResult(Event.Result.DENY);
        }
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack stack,
            EntityPlayer player,
            List list,
            boolean advancedItemTooltips) {
        Optional<NBTTagList> movesTagOpt = getMoves(stack);
        if (movesTagOpt.isPresent()) {
            NBTTagList movesTag = movesTagOpt.get();
            list.add(StatCollector.translateToLocalFormatted(this.getUnlocalizedName() + ".moves",
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
                            String.format(this.getUnlocalizedName() + ".player.%s.%s",
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
        if (tileCD == null || tileCD.moves.isEmpty()) {
            return stack;
        }
        NBTTagCompound tag = ItemUtils.getOrCreateBlockEntityTag(stack);
        NBTTagList list = new NBTTagList();
        tileCD.writeMovesToNBT(list);
        tag.setTag(TAG_MOVES, list);
        return stack;
    }
    
    public static Optional<NBTTagList> getMoves(ItemStack stack) {
        return ItemUtils.getBlockEntityTag(stack)
                        .filter(tag->tag.hasKey(TAG_MOVES, 9))
                        .map(tag->tag.getTagList(TAG_MOVES, 10));
    }
}
