package ee_man.mod3.plugins.computercraft;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import ee_man.mod3.plugins.computercraft.pte.TileEntityCannonPeripheral;
import ee_man.mod3.tileentity.TileEntityCannon;

public class TileEntityCannonPeripheralProvider implements IPeripheralProvider{
	
	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side){
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityCannon)
			return new TileEntityCannonPeripheral((TileEntityCannon)tile);
		return null;
	}
	
}
