package com.vanym.paniclecraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.network.PacketHandler;
import com.vanym.paniclecraft.network.message.MessageChessChoose;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.network.message.MessageChessNewGame;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.ChessDesk;

@SideOnly(Side.CLIENT)
public class GuiChess extends GuiScreen{
	
	public static final ResourceLocation ButtonsTexture = new ResourceLocation(DEF.MOD_ID, "textures/guis/chessButtons.png");
	
	private GuiButtonForChess1[] Buttons = new GuiButtonForChess1[64];
	
	private TileEntityChessDesk tileChess;
	
	public int select = -1;
	
	public int menuMode = 0;
	
	private GuiTextField whitePlayer;
	
	private GuiTextField blackPlayer;
	
	public GuiChess(TileEntityChessDesk tile){
		this.tileChess = tile;
	}
	
	public void onGuiClosed(){
		Keyboard.enableRepeatEvents(false);
	}
	
	@SuppressWarnings("unchecked")
	public void initGui(){
		int var1 = this.width / 2;
		int var2 = this.height / 2;
		int var3 = var1 - 80;
		int var4 = var2 + 60;
		this.buttonList.clear();
		this.whitePlayer = null;
		this.blackPlayer = null;
		Keyboard.enableRepeatEvents(false);
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				Buttons[i * 8 + j] = new GuiButtonForChess1(i * 8 + j, var3 + 20 * j, var4 - 20 * i, tileChess.desk);
				this.buttonList.add(Buttons[i * 8 + j]);
			}
		}
		if(tileChess.desk.needChoose() > 0 && (tileChess.whitePlayer.equalsIgnoreCase(this.mc.thePlayer.getGameProfile().getName()) || tileChess.whitePlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)))
			this.addWhiteChoose(tileChess.desk.needChoose());
		if(tileChess.desk.needChoose() < 0 && (tileChess.blackPlayer.equalsIgnoreCase(this.mc.thePlayer.getGameProfile().getName()) || tileChess.blackPlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)))
			this.addBlackChoose(-tileChess.desk.needChoose());
		this.buttonList.add(new GuiButtonForChess3(1, var1 + 80, var2 - 80, this.fontRendererObj, this.menuMode, StatCollector.translateToLocal("gui.chess.newGame").trim()));
		this.buttonList.add(new GuiButtonForChess3(2, var1 + 80, var2 - 60, this.fontRendererObj, this.menuMode, StatCollector.translateToLocal("gui.chess.aboutGame").trim()));
		switch(this.menuMode){
			case 1:
				Keyboard.enableRepeatEvents(true);
				whitePlayer = new GuiTextField(this.fontRendererObj, var1 + 110, var2 - 60, 75, 20);
				whitePlayer.setFocused(false);
				whitePlayer.setText(TileEntityChessDesk.ChessPublicPlayer);
				blackPlayer = new GuiTextField(this.fontRendererObj, var1 + 110, var2 - 20, 75, 20);
				blackPlayer.setFocused(false);
				blackPlayer.setText(TileEntityChessDesk.ChessPublicPlayer);
				this.buttonList.add(new GuiButton(0, var1 + 110, var2 + 10, 75, 20, StatCollector.translateToLocal("gui.chess.startGame").trim()));
		}
		reDraw();
	}
	
	public void keyTyped(char par1, int par2){
		if(this.menuMode == 1){
			this.whitePlayer.textboxKeyTyped(par1, par2);
			this.blackPlayer.textboxKeyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}
	
	public void mouseClicked(int par1, int par2, int par3){
		super.mouseClicked(par1, par2, par3);
		if(this.menuMode == 1){
			this.whitePlayer.mouseClicked(par1, par2, par3);
			this.blackPlayer.mouseClicked(par1, par2, par3);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addWhiteChoose(int par1){
		int var1 = this.width / 2;
		int var2 = this.height / 2;
		int var3 = var1 - 130 + par1 * 20;
		int var4 = var2 - 100;
		for(int i = 0; i < 4; i++){
			this.buttonList.add(new GuiButtonForChess2(100 + i, var3 + 20 * i, var4));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addBlackChoose(int par1){
		int var1 = this.width / 2;
		int var2 = this.height / 2;
		int var3 = var1 - 130 + par1 * 20;
		int var4 = var2 + 80;
		for(int i = 0; i < 4; i++){
			this.buttonList.add(new GuiButtonForChess2(-(100 + i), var3 + 20 * i, var4));
		}
	}
	
	public void actionPerformed(GuiButton par1){
		if(par1 instanceof GuiButtonForChess1){
			GuiButtonForChess1 var1 = (GuiButtonForChess1)par1;
			if(!var1.cbs)
				return;
			if(select == -1){
				select = var1.id;
			}
			else
				if(var1.id == select)
					select = -1;
				else{
					tileChess.desk.make((byte)select, (byte)var1.id);
					int var2 = select;
					select = -1;
					PacketHandler.INSTANCE.sendToServer(new MessageChessMove(tileChess.xCoord, (short)tileChess.yCoord, tileChess.zCoord, (byte)var2, (byte)var1.id));
				}
			if(tileChess.desk.needChoose() > 0)
				this.addWhiteChoose(tileChess.desk.needChoose());
			if(tileChess.desk.needChoose() < 0)
				this.addBlackChoose(-tileChess.desk.needChoose());
			reDraw();
		}
		else
			if(par1 instanceof GuiButtonForChess2){
				int var1 = tileChess.desk.needChoose();
				tileChess.desk.desk[ChessDesk.getFromXY(Math.abs(var1) - 1, (var1 > 0 ? 7 : 0))] = (byte)(var1 > 0 ? (Math.abs(par1.id) - 98) : -(Math.abs(par1.id) - 98));
				PacketHandler.INSTANCE.sendToServer(new MessageChessChoose(tileChess.xCoord, (short)tileChess.yCoord, tileChess.zCoord, (byte)((Math.abs(par1.id) - 98))));
				this.initGui();
			}
			else
				if(par1 instanceof GuiButtonForChess3){
					if(this.menuMode == par1.id)
						this.menuMode = 0;
					else
						this.menuMode = par1.id;
					this.initGui();
				}
				else{
					if(par1.id == 0){
						this.select = -1;
						this.menuMode = 0;
						this.tileChess.desk = new ChessDesk();
						this.tileChess.whitePlayer = this.whitePlayer.getText();
						this.tileChess.blackPlayer = this.blackPlayer.getText();
						PacketHandler.INSTANCE.sendToServer(new MessageChessNewGame(tileChess.xCoord, (short)tileChess.yCoord, tileChess.zCoord, this.whitePlayer.getText(), this.blackPlayer.getText()));
						this.initGui();
					}
				}
	}
	
	public void drawScreen(int par1, int par2, float par3){
		this.drawDefaultBackground();
		if(this.menuMode == 1){
			this.whitePlayer.drawTextBox();
			this.blackPlayer.drawTextBox();
			int var1 = this.width / 2;
			int var2 = this.height / 2;
			this.drawString(this.fontRendererObj, StatCollector.translateToLocal("gui.chess.whitePlayer").trim(), var1 + 110, var2 - 74, 16777215);
			this.drawString(this.fontRendererObj, StatCollector.translateToLocal("gui.chess.blackPlayer").trim(), var1 + 110, var2 - 34, 16777215);
		}
		else
			if(this.menuMode == 2){
				int var1 = this.width / 2;
				int var2 = this.height / 2;
				this.drawString(this.fontRendererObj, StatCollector.translateToLocal("gui.chess.whitePlayer").trim(), var1 + 110, var2 - 74, 16777215);
				this.drawString(this.fontRendererObj, tileChess.whitePlayer, var1 + 110, var2 - 64, 16777215);
				this.drawString(this.fontRendererObj, StatCollector.translateToLocal("gui.chess.blackPlayer").trim(), var1 + 110, var2 - 34, 16777215);
				this.drawString(this.fontRendererObj, tileChess.blackPlayer, var1 + 110, var2 - 24, 16777215);
			}
		super.drawScreen(par1, par2, par3);
	}
	
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	public void reDrawButton(int i){
		GuiButtonForChess1 var1 = Buttons[i];
		if(select == -1){
			var1.cbs = (tileChess.desk.isWhiteTurn ? tileChess.desk.desk[i] > 0 && (tileChess.whitePlayer.equalsIgnoreCase(this.mc.thePlayer.getGameProfile().getName()) || tileChess.whitePlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer)) : tileChess.desk.desk[i] < 0 && (tileChess.blackPlayer.equalsIgnoreCase(this.mc.thePlayer.getGameProfile().getName()) || tileChess.blackPlayer.equalsIgnoreCase(TileEntityChessDesk.ChessPublicPlayer))) && tileChess.desk.needChoose() == 0;
			var1.mode = 0;
		}
		else
			if(i == select){
				var1.mode = 3;
				var1.cbs = true;
			}
			else
				if(tileChess.desk.canGoTo((byte)select, (byte)i)){
					var1.mode = 2;
					var1.cbs = true;
				}
				else{
					var1.mode = 0;
					var1.cbs = false;
				}
	}
	
	public void reDraw(){
		for(int i = 0; i < 64; i++){
			reDrawButton(i);
		}
	}
	
	public void updateScreen(){
		if((tileChess.getWorldObj().getTileEntity(tileChess.xCoord, tileChess.yCoord, tileChess.zCoord) == null) || this.mc.thePlayer.getDistanceSq(tileChess.xCoord + 0.5D, tileChess.yCoord + 0.5D, tileChess.zCoord + 0.5D) > 64.0D){
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
		}
		if(this.menuMode == 1){
			this.whitePlayer.updateCursorCounter();
			this.blackPlayer.updateCursorCounter();
		}
	}
}
