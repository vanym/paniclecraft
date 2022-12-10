package ee_man.mod3.client.gui.container;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.container.ContainerRobotPanel;
import ee_man.mod3.utils.Localization;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.network.packet.Packet250CustomPayload;

public class GuiRobotPanel extends GuiContainer{
	
	public static final ResourceLocation GuiTexture0 = new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "robotPanelGui0.png");
	public static final ResourceLocation GuiTexture1 = new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "robotPanelGui1.png");
	
	private static final int bs = 10;
	
	public static int selectedButton = 0;
	
	public ContainerRobotPanel container;
	
	public GuiButton[] select;
	
	public GuiButton[] action;
	
	public GuiButton[] getInfo;
	
	private GuiTextField idTextField;
	
	public GuiRobotPanel(ContainerRobotPanel par1Container){
		super(par1Container);
		container = par1Container;
		this.xSize = 330;
		this.ySize = 256;
	}
	
	@SuppressWarnings("unchecked")
	public void initGui(){
		super.initGui();
		this.buttonList.clear();
		int xc = this.width / 2;
		int yc = this.height / 2;
		int x = xc - this.xSize / 2;
		int y = yc - this.ySize / 2;
		select = new GuiButton[bs];
		for(int i = 0; i < select.length; i++){
			select[i] = new GuiButton(i, x + 8, y + 8 + 21 * i, 100, 20, Localization.get("gui.robotPanel.select." + Integer.toString(i)));
			this.buttonList.add(select[i]);
		}
		action = new GuiButton[bs];
		for(int i = 0; i < action.length; i++){
			action[i] = new GuiButton(bs + i, x + 110, y + 8 + 21 * i, 100, 20, Localization.get("gui.robotPanel.action." + selectedButton + "." + Integer.toString(i)));
			this.buttonList.add(action[i]);
		}
		
		getInfo = new GuiButton[bs];
		for(int i = 0; i < getInfo.length; i++){
			getInfo[i] = new GuiButton(bs + bs + i, x + 212, y + 8 + 21 * i, 100, 20, Localization.get("gui.robotPanel.getInfo." + selectedButton + "." + Integer.toString(i)));
			this.buttonList.add(getInfo[i]);
		}
		
		idTextField = new GuiTextField(this.fontRenderer, x + 8, y + 220, 75, 20);
		idTextField.setFocused(false);
		this.buttonList.add(new GuiButton(bs + bs + bs, x + 85, y + 220, 40, 20, "Set"));
		this.buttonList.add(new GuiButton(bs + bs + bs + 1, x + 130, y + 220, 40, 20, "List"));
		this.buttonList.add(new GuiButton(bs + bs + bs + 2, x + 175, y + 220, 40, 20, "Get"));
		this.checkButtons();
	}
	
	public void checkButtons(){
		for(int i = 0; i < select.length; i++){
			select[i].enabled = !(i == selectedButton);
		}
		
		for(int i = 0; i < action.length; i++){
			action[i].displayString = Localization.get("gui.robotPanel.action." + selectedButton + "." + Integer.toString(i));
		}
		
		for(int i = 0; i < getInfo.length; i++){
			getInfo[i].displayString = Localization.get("gui.robotPanel.getInfo." + selectedButton + "." + Integer.toString(i));
		}
	}
	
	public void keyTyped(char par1, int par2){
		super.keyTyped(par1, par2);
		idTextField.textboxKeyTyped(par1, par2);
		Keyboard.enableRepeatEvents(idTextField.isFocused());
	}
	
	public void mouseClicked(int par1, int par2, int par3){
		super.mouseClicked(par1, par2, par3);
		idTextField.mouseClicked(par1, par2, par3);
	}
	
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		idTextField.drawTextBox();
	}
	
	public void updateScreen(){
		super.updateScreen();
		idTextField.updateCursorCounter();
	}
	
	public void onGuiClosed(){
		Keyboard.enableRepeatEvents(false);
	}
	
	public void actionPerformed(GuiButton par1GuiButton){
		if(par1GuiButton.enabled){
			if(par1GuiButton.id < bs + bs + bs)
				switch(par1GuiButton.id / bs){
					case 0:{
						selectedButton = par1GuiButton.id % bs;
					}
					break;
					case 1:{
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						DataOutputStream data = new DataOutputStream(bytes);
						try{
							data.writeByte(9);
							data.writeBoolean(true);
							data.writeByte(selectedButton);
							data.writeByte(par1GuiButton.id % bs);
						} catch(IOException e){
							e.printStackTrace();
						}
						Packet250CustomPayload packet = new Packet250CustomPayload();
						packet.channel = DefaultProperties.MOD_ID;
						packet.data = bytes.toByteArray();
						packet.length = packet.data.length;
						packet.isChunkDataPacket = false;
						NetClientHandler var5 = this.mc.getNetHandler();
						if(var5 != null){
							var5.addToSendQueue(packet);
						}
					}
					break;
					case 2:{
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						DataOutputStream data = new DataOutputStream(bytes);
						try{
							data.writeByte(9);
							data.writeBoolean(false);
							data.writeByte(selectedButton);
							data.writeByte(par1GuiButton.id % bs);
						} catch(IOException e){
							e.printStackTrace();
						}
						Packet250CustomPayload packet = new Packet250CustomPayload();
						packet.channel = DefaultProperties.MOD_ID;
						packet.data = bytes.toByteArray();
						packet.length = packet.data.length;
						packet.isChunkDataPacket = false;
						NetClientHandler var5 = this.mc.getNetHandler();
						if(var5 != null){
							var5.addToSendQueue(packet);
						}
					}
					break;
				}
			else{
				if(par1GuiButton.id == bs + bs + bs){
					int id = -1;
					try{
						id = Integer.parseInt(this.idTextField.getText());
					} catch(NumberFormatException e){
						return;
					}
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					DataOutputStream data = new DataOutputStream(bytes);
					try{
						data.writeByte(10);
						data.writeBoolean(true);
						data.writeInt(id);
					} catch(IOException e){
						e.printStackTrace();
					}
					Packet250CustomPayload packet = new Packet250CustomPayload();
					packet.channel = DefaultProperties.MOD_ID;
					packet.data = bytes.toByteArray();
					packet.length = packet.data.length;
					packet.isChunkDataPacket = false;
					NetClientHandler var5 = this.mc.getNetHandler();
					if(var5 != null){
						var5.addToSendQueue(packet);
					}
				}
				else
					if(par1GuiButton.id == bs + bs + bs + 1){
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						DataOutputStream data = new DataOutputStream(bytes);
						try{
							data.writeByte(10);
							data.writeBoolean(false);
							data.writeBoolean(true);
						} catch(IOException e){
							e.printStackTrace();
						}
						Packet250CustomPayload packet = new Packet250CustomPayload();
						packet.channel = DefaultProperties.MOD_ID;
						packet.data = bytes.toByteArray();
						packet.length = packet.data.length;
						packet.isChunkDataPacket = false;
						NetClientHandler var5 = this.mc.getNetHandler();
						if(var5 != null){
							var5.addToSendQueue(packet);
						}
					}
					else
						if(par1GuiButton.id == bs + bs + bs + 2){
							ByteArrayOutputStream bytes = new ByteArrayOutputStream();
							DataOutputStream data = new DataOutputStream(bytes);
							try{
								data.writeByte(10);
								data.writeBoolean(false);
								data.writeBoolean(false);
							} catch(IOException e){
								e.printStackTrace();
							}
							Packet250CustomPayload packet = new Packet250CustomPayload();
							packet.channel = DefaultProperties.MOD_ID;
							packet.data = bytes.toByteArray();
							packet.length = packet.data.length;
							packet.isChunkDataPacket = false;
							NetClientHandler var5 = this.mc.getNetHandler();
							if(var5 != null){
								var5.addToSendQueue(packet);
							}
						}
			}
		}
		this.checkButtons();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(GuiTexture0);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, 256, 256);
		this.mc.renderEngine.bindTexture(GuiTexture1);
		this.drawTexturedModalRect(k + 256, l, 192, 0, 64, 256);
	}
	
}
