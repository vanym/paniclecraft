package ee_man.mod3.plugins.computercraft.turtle.peripheral;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;
import dan200.turtle.api.ITurtleAccess;
import ee_man.mod3.items.ItemPaintBrush;
import ee_man.mod3.tileEntity.TileEntityPainting;

public class PeripheralPaintBrush implements IHostedPeripheral{
	
	private final ITurtleAccess turtle;
	
	public PeripheralPaintBrush(ITurtleAccess par1Turtle){
		this.turtle = par1Turtle;
	}
	
	@Override
	public String getType(){
		return "paintBrush";
	}
	
	@Override
	public String[] getMethodNames(){
		return new String[]{"getRow", "useBrush"};
	}
	
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception{
		World var1World = turtle.getWorld();
		Vec3 var2Vec3 = turtle.getPosition();
		int d = turtle.getFacingDir();
		int x = (int)var2Vec3.xCoord;
		int y = (int)var2Vec3.yCoord;
		int z = (int)var2Vec3.zCoord;
		int tx = x;
		int ty = y;
		int tz = z;
		switch(d){
			case 2:
				tz--;
			break;
			case 3:
				tz++;
			break;
			case 4:
				tx--;
			break;
			case 5:
				tx++;
			break;
		}
		TileEntity tile = var1World.getBlockTileEntity(tx, ty, tz);
		if(tile == null || !(tile instanceof TileEntityPainting))
			return null;
		TileEntityPainting tileP = (TileEntityPainting)tile;
		switch(method){
			case 0:
				return new Object[]{tileP.Row};
			case 1:
				ItemStack is = turtle.getSlotContents(turtle.getSelectedSlot());
				if(!(is.getItem() instanceof ItemPaintBrush))
					return new Object[]{false};
				ItemPaintBrush ipb = (ItemPaintBrush)is.getItem();
				if(!(arguments[0] instanceof Double) || !(arguments[1] instanceof Double))
					throw new Exception("arguments must be a number");
				int px = ((Double)arguments[0]).intValue();
				int py = ((Double)arguments[1]).intValue();
				if(px < 0 || px > tileP.Row || py < 0 || py > tileP.Row)
					return new Object[]{false};
				ipb.usePaintBrush(is, tileP, px, py);
				return new Object[]{true};
		}
		return null;
	}
	
	@Override
	public boolean canAttachToSide(int side){
		return true;
	}
	
	@Override
	public void attach(IComputerAccess computer){
	}
	
	@Override
	public void detach(IComputerAccess computer){
	}
	
	@Override
	public void update(){
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound){
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound){
	}
	
}
