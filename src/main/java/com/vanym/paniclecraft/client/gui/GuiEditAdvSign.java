package com.vanym.paniclecraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.network.PacketHandler;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.proxy.ClientProxy;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

@SideOnly(Side.CLIENT)
public class GuiEditAdvSign extends GuiScreen{
	/** The title string that is displayed in the top-center of the screen. */
	protected String screenTitle = "Edit sign message:";
	
	/** Reference to the sign object. */
	private TileEntityAdvSign entitySign;
	
	/** Counts the number of screen updates. */
	private int updateCounter;
	
	/** The number of the line that is being edited. */
	private int editLine = 0;
	
	private GuiButton buttonMinus;
	
	private GuiButton buttonPlus;
	
	private GuiButton buttonMinusRed;
	
	private GuiButton buttonPlusRed;
	
	private GuiButton buttonMinusGreen;
	
	private GuiButton buttonPlusGreen;
	
	private GuiButton buttonMinusBlue;
	
	private GuiButton buttonPlusBlue;
	
	private GuiButton buttonCleanColor;
	
	private GuiButton buttonCleanText;
	
	public GuiEditAdvSign(TileEntityAdvSign par1TileEntityAdvSign){
		this.entitySign = par1TileEntityAdvSign;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	public void initGui(){
		buttonMinus = new GuiButton(1, this.width / 2 + 80, this.height / 4 + 99, 20, 20, "-");
		buttonPlus = new GuiButton(2, this.width / 2 + 59, this.height / 4 + 99, 20, 20, "+");
		buttonMinusBlue = new GuiButton(10, this.width / 2 + 80 + 30, this.height / 4 + 78, 20, 20, "-");
		buttonPlusBlue = new GuiButton(11, this.width / 2 + 59 + 30, this.height / 4 + 78, 20, 20, "+");
		buttonMinusGreen = new GuiButton(8, this.width / 2 + 80 + 30, this.height / 4 + 57, 20, 20, "-");
		buttonPlusGreen = new GuiButton(9, this.width / 2 + 59 + 30, this.height / 4 + 57, 20, 20, "+");
		buttonMinusRed = new GuiButton(6, this.width / 2 + 80 + 30, this.height / 4 + 36, 20, 20, "-");
		buttonPlusRed = new GuiButton(7, this.width / 2 + 59 + 30, this.height / 4 + 36, 20, 20, "+");
		buttonCleanColor = new GuiButton(12, this.width / 2 + 80 + 30, this.height / 4 + 15, 20, 20, "CC");
		buttonCleanText = new GuiButton(13, this.width / 2 + 59 + 30, this.height / 4 + 15, 20, 20, "CT");
		if(this.entitySign.getLines() == 1)
			buttonMinus.enabled = false;
		else
			if(this.entitySign.getLines() == 32)
				buttonPlus.enabled = false;
		this.buttonList.clear();
		Keyboard.enableRepeatEvents(true);
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));
		this.buttonList.add(buttonMinus);
		this.buttonList.add(buttonPlus);
		this.buttonList.add(buttonMinusRed);
		this.buttonList.add(buttonPlusRed);
		this.buttonList.add(buttonMinusGreen);
		this.buttonList.add(buttonPlusGreen);
		this.buttonList.add(buttonMinusBlue);
		this.buttonList.add(buttonPlusBlue);
		this.buttonList.add(buttonCleanColor);
		this.buttonList.add(buttonCleanText);
		this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 99, 20, 20, "+" + (char)167));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 79, this.height / 4 + 99, 30, 20, "Copy"));
		this.buttonList.add(new GuiButton(5, this.width / 2 - 48, this.height / 4 + 99, 30, 20, "Paste"));
		this.entitySign.setEditable(false);
	}
	
	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat
	 * events
	 */
	public void onGuiClosed(){
		Keyboard.enableRepeatEvents(false);
		PacketHandler.INSTANCE.sendToServer(new MessageAdvSignChange(this.entitySign));
		this.entitySign.setEditable(true);
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen(){
		++this.updateCounter;
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton){
		if(par1GuiButton.enabled){
			if(par1GuiButton.id == 0){
				this.entitySign.markDirty();
				this.mc.displayGuiScreen((GuiScreen)null);
			}
			else
				
				if(par1GuiButton.id == 1){
					if(this.entitySign.getLines() != 1){
						String[] var1 = this.entitySign.signText.split(TileEntityAdvSign.separator, this.entitySign.getLines());
						String var2 = "";
						if(this.editLine > this.entitySign.getLines() - 2)
							this.editLine = this.entitySign.getLines() - 2;
						for(int var3 = 0; var3 < this.entitySign.getLines() - 1; ++var3){
							char[] var4 = var1[var3].toCharArray();
							String var5 = "";
							for(int var6 = 0; var6 < var4.length; var6++){
								if(canAddChar(var5, this.entitySign.getLines() - 1, var4[var6])){
									var5 = var5 + var4[var6];
								}
							}
							var2 = var2 + var5;
							if(var3 != this.entitySign.getLines() - 2)
								var2 = var2 + TileEntityAdvSign.separator;
						}
						this.entitySign.signText = var2;
						if(this.entitySign.getLines() == 31){
							buttonPlus.enabled = true;
						}
						else
							if(this.entitySign.getLines() == 1){
								buttonMinus.enabled = false;
							}
					}
				}
				else
					
					if(par1GuiButton.id == 2){
						if(this.entitySign.getLines() != 32){
							this.entitySign.signText = this.entitySign.signText + TileEntityAdvSign.separator;
						}
						if(this.entitySign.getLines() == 32){
							buttonPlus.enabled = false;
						}
						else
							if(this.entitySign.getLines() == 2){
								buttonMinus.enabled = true;
							}
					}
					else
						
						if(par1GuiButton.id == 3){
							String par1 = this.entitySign.getLine(this.editLine);
							if(canAddChar(par1, this.entitySign.getLines(), (char)167))
								this.entitySign.editLine(this.editLine, par1 + (char)167);
						}
						else
							
							if(par1GuiButton.id == 4){
								GuiScreen.setClipboardString(this.entitySign.signText);
							}
							else
								
								if(par1GuiButton.id == 5){
									setSignText(GuiScreen.getClipboardString());
								}
								else
									if(par1GuiButton.id == 6){
										if(GuiScreen.isCtrlKeyDown())
											this.entitySign.red -= 50;
										else
											if(GuiScreen.isShiftKeyDown())
												this.entitySign.red -= 10;
											else
												this.entitySign.red--;
									}
									else
										if(par1GuiButton.id == 7){
											if(GuiScreen.isCtrlKeyDown())
												this.entitySign.red += 50;
											else
												if(GuiScreen.isShiftKeyDown())
													this.entitySign.red += 10;
												else
													this.entitySign.red++;
										}
										else
											if(par1GuiButton.id == 8){
												if(GuiScreen.isCtrlKeyDown())
													this.entitySign.green -= 50;
												else
													if(GuiScreen.isShiftKeyDown())
														this.entitySign.green -= 10;
													else
														this.entitySign.green--;
											}
											else
												if(par1GuiButton.id == 9){
													if(GuiScreen.isCtrlKeyDown())
														this.entitySign.green += 50;
													else
														if(GuiScreen.isShiftKeyDown())
															this.entitySign.green += 10;
														else
															this.entitySign.green++;
												}
												else
													if(par1GuiButton.id == 10){
														if(GuiScreen.isCtrlKeyDown())
															this.entitySign.blue -= 50;
														else
															if(GuiScreen.isShiftKeyDown())
																this.entitySign.blue -= 10;
															else
																this.entitySign.blue--;
													}
													else
														if(par1GuiButton.id == 11){
															if(GuiScreen.isCtrlKeyDown())
																this.entitySign.blue += 50;
															else
																if(GuiScreen.isShiftKeyDown())
																	this.entitySign.blue += 10;
																else
																	this.entitySign.blue++;
														}
														else
															if(par1GuiButton.id == 12){
																this.entitySign.red = 127;
																this.entitySign.green = 127;
																this.entitySign.blue = 127;
															}
															else
																if(par1GuiButton.id == 13){
																	this.entitySign.signText = TileEntityAdvSign.separator + TileEntityAdvSign.separator + TileEntityAdvSign.separator + TileEntityAdvSign.separator;
																}
		}
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2){
		switch(par1){
			case 3:
				GuiScreen.setClipboardString(this.entitySign.getLine(this.editLine));
			break;
			case 22:
				this.addText(GuiScreen.getClipboardString());
			break;
			case 24:
				GuiScreen.setClipboardString(this.entitySign.getLine(this.editLine));
				this.entitySign.editLine(this.editLine, "");
			break;
			default:
				switch(par2){
					case 14:
						String var4 = this.entitySign.getLine(this.editLine);
						if(var4.length() > 0){
							if(GuiScreen.isCtrlKeyDown())
								this.entitySign.editLine(this.editLine, "");
							else
								this.entitySign.editLine(this.editLine, var4.substring(0, var4.length() - 1));
						}
					break;
					case 200:
						--this.editLine;
						if(this.editLine < 0)
							this.editLine = this.entitySign.getLines() - 1;
					break;
					case 208:
					case 28:
						++this.editLine;
						if(this.editLine > this.entitySign.getLines() - 1)
							this.editLine = 0;
					break;
					case 211:
						String var5 = this.entitySign.getLine(this.editLine);
						if(var5.length() > 0){
							this.entitySign.editLine(this.editLine, var5.substring(1, var5.length()));
						}
					break;
					/*
					 * case 199: if (GuiScreen.isShiftKeyDown()) {
					 * this.setSelectionPos(0); } else {
					 * this.setCursorPositionZero(); } break; case 203: if
					 * (GuiScreen.isShiftKeyDown()) { if
					 * (GuiScreen.isCtrlKeyDown()) {
					 * this.setSelectionPos(this.getNthWordFromPos(-1,
					 * this.getSelectionEnd())); } else {
					 * this.setSelectionPos(this.getSelectionEnd() - 1); } }
					 * else if (GuiScreen.isCtrlKeyDown()) {
					 * this.setCursorPosition(this.getNthWordFromCursor(-1)); }
					 * else { this.moveCursorBy(-1); } break; case 205: if
					 * (GuiScreen.isShiftKeyDown()) { if
					 * (GuiScreen.isCtrlKeyDown()) {
					 * this.setSelectionPos(this.getNthWordFromPos(1,
					 * this.getSelectionEnd())); } else {
					 * this.setSelectionPos(this.getSelectionEnd() + 1); } }
					 * else if (GuiScreen.isCtrlKeyDown()) {
					 * this.setCursorPosition(this.getNthWordFromCursor(1)); }
					 * else { this.moveCursorBy(1); }
					 * 
					 * return true; case 207: if (GuiScreen.isShiftKeyDown()) {
					 * this.setSelectionPos(this.text.length()); } else {
					 * this.setCursorPositionEnd(); }
					 * 
					 * return true; case 211: if (GuiScreen.isCtrlKeyDown()) {
					 * this.deleteWords(1); } else { this.deleteFromCursor(1); }
					 * 
					 * return true;
					 */
					default:
						this.addText(Character.toString(par1));
				}
		}/* ; */
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3){
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 40, 16777215);
		this.drawString(this.fontRendererObj, "Lines:" + this.entitySign.getLines(), this.width / 2 + 19, this.height / 4 + 110, 16777215);
		this.drawString(this.fontRendererObj, "B:" + (this.entitySign.blue + 128), this.width / 2 + 29 + 30, this.height / 4 + 89, 16777215);
		this.drawString(this.fontRendererObj, "G:" + (this.entitySign.green + 128), this.width / 2 + 29 + 30, this.height / 4 + 68, 16777215);
		this.drawString(this.fontRendererObj, "R:" + (this.entitySign.red + 128), this.width / 2 + 29 + 30, this.height / 4 + 47, 16777215);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(this.width / 2), 0.0F, 50.0F);
		float var4 = 93.75F;
		GL11.glScalef(-var4, -var4, -var4);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		BlockAdvSign var5 = (BlockAdvSign)this.entitySign.getBlockType();
		
		if(var5 == ModItems.blockAdvSignPost){
			float var6 = (float)(this.entitySign.getBlockMetadata() * 360) / 16.0F;
			GL11.glRotatef(var6, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
		}
		else{
			int var8 = this.entitySign.getBlockMetadata();
			float var7 = 0.0F;
			
			if(var8 == 2){
				var7 = 180.0F;
			}
			
			if(var8 == 4){
				var7 = 90.0F;
			}
			
			if(var8 == 5){
				var7 = -90.0F;
			}
			
			GL11.glRotatef(var7, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
		}
		
		if(this.updateCounter / 6 % 2 == 0){
			this.entitySign.lineBeingEdited = this.editLine;
		}
		
		ClientProxy.tileAdvSignRenderer.renderTileEntityAt(this.entitySign, -0.5D, -0.75D, -0.5D, 0.0F);
		this.entitySign.lineBeingEdited = -1;
		GL11.glPopMatrix();
		super.drawScreen(par1, par2, par3);
	}
	
	private void addText(char par1){
		addText(Character.toString(par1));
	}
	
	private void addText(String par1){
		String var1 = this.entitySign.getLine(this.editLine);
		char[] var2 = par1.toCharArray();
		for(int var3 = 0; var3 < var2.length; var3++){
			if((canAddChar(var1, this.entitySign.getLines(), var2[var3])) && ((ChatAllowedCharacters.isAllowedCharacter(var2[var3]) && var2[var3] != TileEntityAdvSign.separatorChar) || (var2[var3] == 167)))
				var1 = var1 + var2[var3];
		}
		this.entitySign.editLine(this.editLine, var1);
	}
	
	private void addText(char par1, int par2){
		addText(Character.toString(par1), par2);
	}
	
	private void addText(String par1, int par2){
		String var1 = this.entitySign.getLine(par2);
		char[] var2 = par1.toCharArray();
		for(int var3 = 0; var3 < var2.length; var3++){
			if((canAddChar(var1, this.entitySign.getLines(), var2[var3])) && ((ChatAllowedCharacters.isAllowedCharacter(var2[var3]) && var2[var3] != TileEntityAdvSign.separatorChar) || (var2[var3] == 167)))
				var1 = var1 + var2[var3];
		}
		this.entitySign.editLine(par2, var1);
	}
	
	/*
	 * private String getLine(int par1){ String[] var1 =
	 * this.entitySign.signText
	 * .split(this.entitySign.separator,this.entitySign.lines); return
	 * var1[par1]; }
	 * 
	 * private void editLine(int par1,String par2){ String[] var1 =
	 * this.entitySign
	 * .signText.split(this.entitySign.separator,this.entitySign.lines);
	 * var1[par1] = par2; String var3 = ""; for(int var2 = 0;var2 <
	 * this.entitySign.lines;++var2){ var3 = var3 + var1[var2]; if(var2 !=
	 * this.entitySign.lines - 1)var3 = var3 + this.entitySign.separator; }
	 * this.entitySign.signText = var3; }
	 */
	
	/*
	 * private boolean canAddChar(String par1,int par2){ Iterator var10 =
	 * this.fontRenderer.listFormattedStringToWidth(par1, 6 * ((int)((float)15 /
	 * ((float)4 / (float)par2)))).iterator(); String var6 =
	 * (String)var10.next(); return !(var6.length() < par1.length()); }
	 */
	
	private boolean canAddChar(String par1, int par2, char par3){
		return(this.fontRendererObj.getStringWidth(par1 + par3) < (int)((float)92 / ((float)4 / (float)par2)));
	}
	
	private void setSignText(String par1){
		String[] var1 = par1.split(TileEntityAdvSign.separator, (par1 + '\u0000').split(TileEntityAdvSign.separator).length);
		String var2 = "";
		for(int var3 = 1; var3 < var1.length && var3 < 32; var3++){
			var2 = var2 + TileEntityAdvSign.separator;
		}
		this.entitySign.signText = var2;
		int var5 = this.entitySign.getLines();
		for(int var4 = 0; var4 < var5; var4++){
			addText(var1[var4], var4);
		}
		if(var5 == 1)
			buttonMinus.enabled = false;
		else{
			buttonMinus.enabled = true;
		}
		if(var5 == 32)
			buttonPlus.enabled = false;
		else{
			buttonPlus.enabled = true;
		}
	}
}
