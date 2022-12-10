package ee_man.mod3.plugins.computercraft.turtle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleSide;
import dan200.turtle.api.TurtleUpgradeType;
import dan200.turtle.api.TurtleVerb;
import ee_man.mod3.Core;
import ee_man.mod3.plugins.computercraft.turtle.peripheral.PeripheralSignEdit;

public class TurtleSignEdit implements ITurtleUpgrade{
	
	@SideOnly(Side.CLIENT)
	public static Icon icon;
	
	@Override
	public int getUpgradeID(){
		return 246;
	}
	
	@Override
	public String getAdjective(){
		return "SignEdit";
	}
	
	@Override
	public TurtleUpgradeType getType(){
		return TurtleUpgradeType.Peripheral;
	}
	
	@Override
	public ItemStack getCraftingItem(){
		return new ItemStack(Core.itemSignEditTool, 0);
	}
	
	@Override
	public boolean isSecret(){
		return false;
	}
	
	@Override
	public IHostedPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side){
		return new PeripheralSignEdit(turtle);
	}
	
	@Override
	public boolean useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction){
		return false;
	}
	
	@Override
	public Icon getIcon(ITurtleAccess turtle, TurtleSide side){
		return icon;
	}
	
}
