package ee_man.mod3.plugins.computercraft.turtle.peripheral;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;
import dan200.turtle.api.ITurtleAccess;
import ee_man.mod3.tileEntity.TileEntityAdvSign;

public class PeripheralSignEdit implements IHostedPeripheral{
	
	private final ITurtleAccess turtle;
	
	public PeripheralSignEdit(ITurtleAccess par1Turtle){
		this.turtle = par1Turtle;
	}
	
	@Override
	public String getType(){
		return "signEdit";
	}
	
	@Override
	public String[] getMethodNames(){
		return new String[]{"getText", "getTextDown", "getTextUp", "isAdv", "isAdvDown", "isAdvUp", "setText", "setTextDown", "setTextUp"};
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
		if(method % 3 == 0){
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
		}
		else
			if(method % 3 == 1)
				ty--;
			else
				if(method % 3 == 2)
					ty++;
		TileEntity tile = var1World.getBlockTileEntity(tx, ty, tz);
		if(method / 3 == 0){
			if(tile instanceof TileEntitySign){
				TileEntitySign tileS = (TileEntitySign)tile;
				return (Object[])tileS.signText;
			}
			else
				if(tile instanceof TileEntityAdvSign){
					TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
					return (Object[])tileAS.signText.split(TileEntityAdvSign.separator, tileAS.getLines());
				}
		}
		else
			if(method / 3 == 1){
				if(tile instanceof TileEntityAdvSign || tile instanceof TileEntitySign){
					return new Object[]{tile instanceof TileEntityAdvSign};
				}
			}
			else
				if(method / 3 == 2){
					for(int i = 0; i < arguments.length; i++)
						if(!(arguments[i] instanceof String))
							throw new Exception("arguments must be a string");
					if(arguments.length < 1)
						throw new Exception("too few arguments");
					String[] strAr = new String[arguments.length];
					for(int i = 0; i < arguments.length; i++)
						strAr[i] = (String)arguments[i];
					if(tile instanceof TileEntitySign){
						TileEntitySign tileS = (TileEntitySign)tile;
						if(strAr.length != tileS.signText.length)
							return new Object[]{false};
						for(int i = 0; i < tileS.signText.length; i++){
							strAr[i] = strAr[i].substring(0, 15);
							tileS.signText[i] = strAr[i];
						}
						tileS.worldObj.markBlockForUpdate(tx, ty, tz);
						return new Object[]{true};
					}
					else
						if(tile instanceof TileEntityAdvSign){
							TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
							String strToSet = "";
							int m = (int)(3.75F * (float)strAr.length);
							for(int i = 0; i < strAr.length; i++){
								strAr[i] = strAr[i].substring(0, m);
							}
							for(int i = 0; i < strAr.length && i <= 32; i++){
								strToSet += strAr[i];
								if(i + 1 < strAr.length)
									strToSet += TileEntityAdvSign.separator;
							}
							tileAS.signText = strToSet;
							tileAS.worldObj.markBlockForUpdate(tx, ty, tz);
							return new Object[]{true};
						}
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
