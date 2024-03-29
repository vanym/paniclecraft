package com.vanym.paniclecraft.item;

import java.util.function.Supplier;

import com.vanym.paniclecraft.client.renderer.item.ItemRendererPortableWorkbench;
import com.vanym.paniclecraft.container.ContainerPortableWorkbench;

import net.minecraft.block.CraftingTableBlock;
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
import net.minecraft.world.World;

public class ItemWorkbench extends Item implements INamedContainerProvider {
    
    public final Supplier<Integer> durability;
    
    public ItemWorkbench(Supplier<Integer> durability) {
        super(Props.create()
                   .maxDamage(8192)
                   .setTEISR(()->ItemRendererPortableWorkbench::new));
        this.setRegistryName("portable_workbench");
        this.durability = durability;
    }
    
    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.durability.get();
    }
    
    @Override
    public boolean isDamageable() {
        return this.durability.get() > 0;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World world,
            PlayerEntity player,
            Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            player.openContainer(this);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
    
    public static boolean canBeWorkbench(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemWorkbench && !stack.isEmpty();
    }
    
    @Override
    public int getBurnTime(ItemStack fuel) {
        return 200;
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ContainerPortableWorkbench(id, inventory);
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return CraftingTableBlock.field_220271_a;
    }
}
