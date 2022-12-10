package ee_man.mod3.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import ee_man.mod3.Core;
import ee_man.mod3.container.ContainerCannon;
import ee_man.mod3.container.ContainerPalette;
import ee_man.mod3.container.ContainerPrivateChest;
import ee_man.mod3.core.Mod3Event;
import ee_man.mod3.core.Mod3Hooks;
import ee_man.mod3.items.ItemPaintBrush;
import ee_man.mod3.items.ItemPrivateChestUpgrade;
import ee_man.mod3.tileEntity.TileEntityAdvSign;
import ee_man.mod3.tileEntity.TileEntityCannon;
import ee_man.mod3.tileEntity.TileEntityChessDesk;
import ee_man.mod3.tileEntity.TileEntityPainting;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;
import ee_man.mod3.utils.ChessDesk;

public class PacketHandlerServer implements IPacketHandler{
	
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
						if(var4.isEditable()){
							byte red = data.readByte();
							byte green = data.readByte();
							byte blue = data.readByte();
							String text = Packet.readString(data, Integer.MAX_VALUE);
							Mod3Event.OnAdvSignPacket event = Mod3Hooks.OnAdvSignPacket(var4, text, red, green, blue, playerEntity);
							if(!event.isCanceled()){
								var4.signText = event.textToSet;
								var4.red = event.red;
								var4.green = event.green;
								var4.blue = event.blue;
								var4.onInventoryChanged();
								event.tileAdvSign.worldObj.markBlockForUpdate(event.tileAdvSign.xCoord, event.tileAdvSign.yCoord, event.tileAdvSign.zCoord);
							}
						}
					}
				}
			}
			else
				if(ID == 2){
					int x = data.readInt();
					int y = data.readInt();
					int z = data.readInt();
					TileEntity var3 = world.getBlockTileEntity(x, y, z);
					ItemStack is = playerEntity.inventory.getCurrentItem();
					if(var3 instanceof TileEntityPainting && (is != null ? is.getItem() instanceof ItemPaintBrush : false)){
						TileEntityPainting var4 = (TileEntityPainting)var3;
						int px = data.read();
						int py = data.read();
						if(px >= 0 && px < var4.Row && py >= 0 && py < var4.Row && !ForgeEventFactory.onPlayerInteract(playerEntity, Action.RIGHT_CLICK_BLOCK, x, y, z, var4.getBlockMetadata()).isCanceled()){
							Mod3Event.OnPaintBrushPacket event = Mod3Hooks.OnPaintBrushPacket(var4, playerEntity, px, py, is);
							if(!event.isCanceled())
								((ItemPaintBrush)is.getItem()).usePaintBrush(event.itemStack, event.tileP, event.x, event.y);
						}
					}
				}
				else
					if(ID == 3){
						int x = data.readInt();
						int y = data.readInt();
						int z = data.readInt();
						TileEntity var3 = world.getBlockTileEntity(x, y, z);
						if(var3 instanceof TileEntityChessDesk && playerEntity.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D){
							TileEntityChessDesk var4 = (TileEntityChessDesk)var3;
							byte from = data.readByte();
							byte to = data.readByte();
							Mod3Event.OnChessPacket.MakeMove event = Mod3Hooks.OnChessPacketMakeMove(var4, playerEntity, from, to);
							if(!event.isCanceled() && event.tileChessDesk.desk.canGoTo(event.from, event.to) && event.tileChessDesk.desk.needChoose() == 0 && (event.tileChessDesk.desk.isWhiteTurn ? event.tileChessDesk.desk.desk[event.from] > 0 && (event.tileChessDesk.whitePlayer.equals(playerEntity.username) || event.tileChessDesk.whitePlayer.equals(TileEntityChessDesk.ChessPublicPlayer)) : event.tileChessDesk.desk.desk[event.from] < 0 && (event.tileChessDesk.blackPlayer.equals(playerEntity.username) || event.tileChessDesk.blackPlayer.equals(TileEntityChessDesk.ChessPublicPlayer)))){
								event.tileChessDesk.desk.make(event.from, event.to);
								event.tileChessDesk.worldObj.markBlockForUpdate(event.tileChessDesk.xCoord, event.tileChessDesk.yCoord, event.tileChessDesk.zCoord);
							}
						}
					}
					else
						if(ID == 4){
							int x = data.readInt();
							int y = data.readInt();
							int z = data.readInt();
							TileEntity var3 = world.getBlockTileEntity(x, y, z);
							if(var3 instanceof TileEntityChessDesk && playerEntity.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D){
								TileEntityChessDesk var4 = (TileEntityChessDesk)var3;
								byte var2 = data.readByte();
								Mod3Event.OnChessPacket.MakeChoose event = Mod3Hooks.OnChessPacketMakeChoose(var4, playerEntity, var2);
								if(!event.isCanceled() && event.tileChessDesk.desk.needChoose() != 0 && event.choose > 1 && event.choose < 6 && (event.tileChessDesk.desk.needChoose() > 0 ? (event.tileChessDesk.whitePlayer.equals(playerEntity.username) || event.tileChessDesk.whitePlayer.equals(TileEntityChessDesk.ChessPublicPlayer)) : (event.tileChessDesk.blackPlayer.equals(playerEntity.username) || event.tileChessDesk.blackPlayer.equals(TileEntityChessDesk.ChessPublicPlayer)))){
									int var1 = event.tileChessDesk.desk.needChoose();
									event.tileChessDesk.desk.desk[ChessDesk.getFromXY(Math.abs(var1) - 1, (var1 > 0 ? 7 : 0))] = (byte)(var1 > 0 ? event.choose : -event.choose);
									event.tileChessDesk.worldObj.markBlockForUpdate(event.tileChessDesk.xCoord, event.tileChessDesk.yCoord, event.tileChessDesk.zCoord);
								}
							}
						}
						else
							if(ID == 5){
								int x = data.readInt();
								int y = data.readInt();
								int z = data.readInt();
								TileEntity var3 = world.getBlockTileEntity(x, y, z);
								if(var3 instanceof TileEntityChessDesk && playerEntity.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D){
									TileEntityChessDesk var4 = (TileEntityChessDesk)var3;
									String var1 = Packet.readString(data, 30);
									String var2 = Packet.readString(data, 30);
									Mod3Event.OnChessPacket.ResetGame event = Mod3Hooks.OnChessPacketResetGame(var4, playerEntity, var1, var2);
									if(!event.isCanceled()){
										event.tileChessDesk.desk = new ChessDesk();
										event.tileChessDesk.whitePlayer = event.whitePlayer;
										event.tileChessDesk.blackPlayer = event.blackPlayer;
										event.tileChessDesk.worldObj.markBlockForUpdate(event.tileChessDesk.xCoord, event.tileChessDesk.yCoord, event.tileChessDesk.zCoord);
									}
								}
							}
							else
								if(ID == 6){
									if(playerEntity.openContainer instanceof ContainerCannon){
										TileEntityCannon var4 = ((ContainerCannon)playerEntity.openContainer).tileCannon;
										int ac = data.readByte();
										Mod3Event.OnCannonPacket event = Mod3Hooks.OnCannonPacket(var4, playerEntity, ac);
										if(!event.isCanceled()){
											switch(event.action){
												case 0:
													event.tileCannon.direction--;
													while(event.tileCannon.direction < 0)
														event.tileCannon.direction += 360;
												break;
												case 1:
													event.tileCannon.direction++;
													while(event.tileCannon.direction >= 360)
														event.tileCannon.direction -= 360;
												break;
												case 2:
													if(event.tileCannon.height > 0)
														event.tileCannon.height--;
												break;
												case 3:
													if(event.tileCannon.height < 90)
														event.tileCannon.height++;
												break;
												case 4:
													event.tileCannon.strength--;
													if(event.tileCannon.strength < 0)
														event.tileCannon.strength = 0;
												break;
												case 5:
													event.tileCannon.strength++;
													if(event.tileCannon.strength > event.tileCannon.maxStrength)
														event.tileCannon.strength = event.tileCannon.maxStrength;
												break;
												case 0 + 6:
													event.tileCannon.direction -= 10;
													while(event.tileCannon.direction < 0)
														event.tileCannon.direction += 360;
												break;
												case 1 + 6:
													event.tileCannon.direction += 10;
													while(event.tileCannon.direction >= 360)
														event.tileCannon.direction -= 360;
												break;
												case 2 + 6:
													event.tileCannon.height -= 10;
													if(event.tileCannon.height < 0)
														event.tileCannon.height = 0;
												break;
												case 3 + 6:
													event.tileCannon.height += 10;
													if(event.tileCannon.height > 90)
														event.tileCannon.height = 90;
												break;
												case 4 + 6:
													event.tileCannon.strength -= 5;
													if(event.tileCannon.strength < 0)
														event.tileCannon.strength = 0;
												break;
												case 5 + 6:
													event.tileCannon.strength += 5;
													if(event.tileCannon.strength > event.tileCannon.maxStrength)
														event.tileCannon.strength = event.tileCannon.maxStrength;
												break;
												case 0 + 12:
													event.tileCannon.direction -= 45;
													while(event.tileCannon.direction < 0)
														event.tileCannon.direction += 360;
												break;
												case 1 + 12:
													event.tileCannon.direction += 45;
													while(event.tileCannon.direction >= 360)
														event.tileCannon.direction -= 360;
												break;
												case 2 + 12:
													event.tileCannon.height -= 45;
													if(event.tileCannon.height < 0)
														event.tileCannon.height = 0;
												break;
												case 3 + 12:
													int tmpHeight = event.tileCannon.height;
													tmpHeight += 45;
													if(tmpHeight > 90)
														tmpHeight = 90;
													event.tileCannon.height = (byte)tmpHeight;
												break;
												case 4 + 12:
													event.tileCannon.strength -= 10;
													if(event.tileCannon.strength < 0)
														event.tileCannon.strength = 0;
												break;
												case 5 + 12:
													event.tileCannon.strength += 10;
													if(event.tileCannon.strength > event.tileCannon.maxStrength)
														event.tileCannon.strength = event.tileCannon.maxStrength;
												break;
											}
											event.tileCannon.worldObj.markBlockForUpdate(event.tileCannon.xCoord, event.tileCannon.yCoord, event.tileCannon.zCoord);
										}
									}
								}
								else
									if(ID == 7){
										if(playerEntity.openContainer instanceof ContainerPrivateChest){
											TileEntityPrivateChest var4 = ((ContainerPrivateChest)playerEntity.openContainer).tile;
											ItemStack su = var4.getSelectedUpgrade();
											if(su != null && Core.itemPrivateChestUpgrade != null && su.itemID == Core.itemPrivateChestUpgrade.itemID && su.getItemDamage() == 0){
												int type = data.read();
												if(type == 0){
													String name = Packet.readString(data, 30);
													if(!ItemPrivateChestUpgrade.getPlayerNamesFromNBT(var4.upgradesDataNotSendable).contains(name) && !name.equals(""))
														ItemPrivateChestUpgrade.addPlayerNameToNBT(var4.upgradesDataNotSendable, name);
												}
												else
													if(type == 1){
														String name = Packet.readString(data, 30);
														ItemPrivateChestUpgrade.removePlayerNameFromNBT(var4.upgradesDataNotSendable, name);
													}
													else
														if(type == 2){
															ArrayList<String> list = ItemPrivateChestUpgrade.getPlayerNamesFromNBT(var4.upgradesDataNotSendable);
															Iterator<String> ir = list.iterator();
															playerEntity.addChatMessage("{");
															while(ir.hasNext())
																playerEntity.addChatMessage("  " + ir.next());
															playerEntity.addChatMessage("}");
														}
											}
										}
									}
									else
										if(ID == 8){
											if(playerEntity.openContainer instanceof ContainerPalette){
												ContainerPalette palette = (ContainerPalette)playerEntity.openContainer;
												int red = palette.inventoryPalette.getRed();
												int green = palette.inventoryPalette.getGreen();
												int blue = palette.inventoryPalette.getBlue();
												int ac = data.readByte();
												switch(ac){
													case 0:
														red--;
													break;
													case 1:
														red++;
													break;
													case 2:
														green--;
													break;
													case 3:
														green++;
													break;
													case 4:
														blue--;
													break;
													case 5:
														blue++;
													break;
													case 0 + 6:
														red -= 10;
													break;
													case 1 + 6:
														red += 10;
													break;
													case 2 + 6:
														green -= 10;
													break;
													case 3 + 6:
														green += 10;
													break;
													case 4 + 6:
														blue -= 10;
													break;
													case 5 + 6:
														blue += 10;
													break;
													case 0 + 12:
														red -= 50;
													break;
													case 1 + 12:
														red += 50;
													break;
													case 2 + 12:
														green -= 50;
													break;
													case 3 + 12:
														green += 50;
													break;
													case 4 + 12:
														blue -= 50;
													break;
													case 5 + 12:
														blue += 50;
													break;
												}
												palette.inventoryPalette.setColor(red, green, blue);
											}
										}
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
