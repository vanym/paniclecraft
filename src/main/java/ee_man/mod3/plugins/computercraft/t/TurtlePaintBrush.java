package ee_man.mod3.plugins.computercraft.t;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;
import ee_man.mod3.init.ModItems;
import ee_man.mod3.plugins.computercraft.t.p.PeripheralPaintBrush;

public class TurtlePaintBrush implements ITurtleUpgrade{
	
	public IIcon iconLeft;
	public IIcon iconRight;
	
	@Override
	public int getUpgradeID(){
		return 245;
	}
	
	@Override
	public String getUnlocalisedAdjective(){
		return "Painter";
	}
	
	@Override
	public TurtleUpgradeType getType(){
		return TurtleUpgradeType.Peripheral;
	}
	
	@Override
	public ItemStack getCraftingItem(){
		return new ItemStack(ModItems.itemPaintBrush);
	}
	
	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side){
		return new PeripheralPaintBrush(turtle);
	}
	
	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction){
		return null;
	}
	
	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side){
		switch(side){
			default:
			case Left:
				return iconLeft;
			case Right:
				return iconRight;
		}
	}
	
	@Override
	public void update(ITurtleAccess turtle, TurtleSide side){
	}
	
}
