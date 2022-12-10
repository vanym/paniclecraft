package ee_man.mod3.client.gui.container;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.container.ContainerPrivateChest;

public class GuiPrivateChestUpgradePrivateTool extends GuiPrivateChest{
	
	private GuiTextField text;
	
	public GuiPrivateChestUpgradePrivateTool(ContainerPrivateChest par1Container){
		super(par1Container);
	}
	
	@SuppressWarnings("unchecked")
	public void initGui(){
		super.initGui();
		int x = this.width / 2 + 28;
		int y = this.height / 2 - 112;
		this.buttonList.add(new GuiButton(0, x + 55, y + 130, 40, 10, "Add"));
		this.buttonList.add(new GuiButton(1, x + 55, y + 100, 40, 10, "Remove"));
		this.buttonList.add(new GuiButton(2, x + 10, y + 100, 40, 10, "List"));
		this.text = new GuiTextField(this.fontRenderer, x, y + 110, 95, 20);
		this.text.setFocused(false);
		this.text.setMaxStringLength(30);
	}
	
	public void actionPerformed(GuiButton par1GuiButton){
		super.actionPerformed(par1GuiButton);
		if(par1GuiButton.enabled){
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream data = new DataOutputStream(bytes);
			try{
				data.writeByte(7);
				data.writeByte(par1GuiButton.id);
				if(par1GuiButton.id == 0 || par1GuiButton.id == 1){
					Packet.writeString(this.text.getText(), data);
				}
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
			switch(par1GuiButton.id){
				case 0:
					
				break;
				case 1:
					
				break;
				case 2:
					
				break;
			}
		}
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int i, int j){
		super.drawGuiContainerBackgroundLayer(f, i, j);
		this.text.drawTextBox();
	}
	
	public void keyTyped(char par1, int par2){
		if(this.text.isFocused())
			this.text.textboxKeyTyped(par1, par2);
		else
			super.keyTyped(par1, par2);
		
	}
	
	public void mouseClicked(int par1, int par2, int par3){
		super.mouseClicked(par1, par2, par3);
		this.text.mouseClicked(par1, par2, par3);
		Keyboard.enableRepeatEvents(this.text.isFocused());
	}
	
	public void updateScreen(){
		super.updateScreen();
		this.text.updateCursorCounter();
	}
	
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
}
