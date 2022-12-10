package ee_man.mod3.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import ee_man.mod3.client.gui.GuiChess;
import ee_man.mod3.client.gui.container.GuiCannon;
import ee_man.mod3.tileEntity.*;

public class PacketHandlerClient implements IPacketHandler{
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player){
		EntityPlayer playerEntity = (EntityPlayer)player;
		World world = playerEntity.worldObj;
		try{
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
			int ID = data.read();
			if(ID == 1){
				int x = data.readInt();
				int y = data.readInt();
				int z = data.readInt();
				if(world.blockExists(x, y, z)){
					TileEntity var3 = world.getBlockTileEntity(x, y, z);
					if(var3 instanceof TileEntityAdvSign){
						TileEntityAdvSign var4 = (TileEntityAdvSign)var3;
						var4.red = data.readByte();
						var4.green = data.readByte();
						var4.blue = data.readByte();
						var4.signText = Packet.readString(data, Integer.MAX_VALUE);
						var4.onInventoryChanged();
					}
				}
			}
			else
				if(ID == 2){
					int x = data.readInt();
					int y = data.readInt();
					int z = data.readInt();
					TileEntity var3 = world.getBlockTileEntity(x, y, z);
					if(var3 instanceof TileEntityPainting){
						TileEntityPainting var4 = (TileEntityPainting)var3;
						int Row = data.readInt();
						var4.BrushRadius = data.readInt();
						int i1 = data.readInt();
						int[][] var5 = new int[2][i1];
						for(int i = 0; i < i1; i++){
							var5[0][i] = data.readInt();
						}
						for(int i = 0; i < i1; i++){
							var5[1][i] = data.readInt();
						}
						var4.Row = Row;
						var4.NoDrawPixels = var5;
						byte[] pic = new byte[Row * Row * 3];
						for(int i = 0; i < Row * Row * 3; i++){
							pic[i] = data.readByte();
						}
						var4.pic = pic;
					}
				}
				else
					if(ID == 3){
						int x = data.readInt();
						int y = data.readInt();
						int z = data.readInt();
						TileEntity var3 = world.getBlockTileEntity(x, y, z);
						if(var3 instanceof TileEntityChessDesk){
							TileEntityChessDesk var4 = (TileEntityChessDesk)var3;
							for(int i = 0; i < 64; i++)
								var4.desk.desk[i] = data.readByte();
							var4.desk.lastFrom = data.readByte();
							var4.desk.lastTo = data.readByte();
							var4.desk.isWhiteTurn = data.readBoolean();
							if(data.readBoolean())
								var4.name = Packet.readString(data, 30);
							var4.whitePlayer = Packet.readString(data, 30);
							var4.blackPlayer = Packet.readString(data, 30);
							GuiScreen gui = Minecraft.getMinecraft().currentScreen;
							if(gui != null)
								if(gui instanceof GuiChess){
									GuiChess guiChess = (GuiChess)gui;
									guiChess.select = -1;
									guiChess.initGui();
								}
						}
					}
					else
						if(ID == 4){
							int x = data.readInt();
							int y = data.readInt();
							int z = data.readInt();
							TileEntity var3 = world.getBlockTileEntity(x, y, z);
							if(var3 instanceof TileEntityCannon){
								TileEntityCannon var4 = (TileEntityCannon)var3;
								var4.direction = data.readShort();
								var4.height = data.readByte();
								var4.strength = data.readByte();
								var4.maxStrength = data.readByte();
								GuiScreen gui = Minecraft.getMinecraft().currentScreen;
								if(gui != null)
									if(gui instanceof GuiCannon){
										GuiCannon guiCannon = (GuiCannon)gui;
										guiCannon.checkHeight();
									}
							}
						}
						else
							if(ID == 5){
								int x = data.readInt();
								int y = data.readInt();
								int z = data.readInt();
								TileEntity var3 = world.getBlockTileEntity(x, y, z);
								if(var3 instanceof TileEntitySaverChest){
									TileEntitySaverChest var4 = (TileEntitySaverChest)var3;
									var4.open = data.readBoolean();
								}
							}
							else
								if(ID == 6){
									int x = data.readInt();
									int y = data.readInt();
									int z = data.readInt();
									TileEntity var3 = world.getBlockTileEntity(x, y, z);
									if(var3 instanceof TileEntityPrivateChest){
										TileEntityPrivateChest var4 = (TileEntityPrivateChest)var3;
										var4.select = data.readByte();
										for(int i = 0; i < var4.inventoryUpgrades.getSizeInventory(); i++){
											var4.inventoryUpgrades.setInventorySlotContents(i, Packet.readItemStack(data));
										}
										var4.upgradesDataSendable = (NBTTagCompound)NBTTagCompound.readNamedTag(data);
									}
								}
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
