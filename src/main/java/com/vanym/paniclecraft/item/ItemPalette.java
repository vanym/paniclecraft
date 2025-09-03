package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.container.ContainerPalette;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemPalette extends Item implements INamedContainerProvider {
    
    public ItemPalette() {
        super(Props.create().stacksTo(1));
        this.setRegistryName("palette");
    }
    
    @Override
    public ActionResult<ItemStack> use(
            World world,
            PlayerEntity player,
            Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            player.openMenu(this);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ContainerPalette(id, inventory);
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getDescriptionId() + ".inventory");
    }
    
    public static boolean canBePalette(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemPalette && !stack.isEmpty();
    }
}
