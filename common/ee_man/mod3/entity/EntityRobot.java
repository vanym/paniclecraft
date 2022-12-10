package ee_man.mod3.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import ee_man.mod3.Core;
import ee_man.mod3.core.Mod3Event;
import ee_man.mod3.core.Mod3Hooks;
import ee_man.mod3.network.FakeNetworkManager;
import ee_man.mod3.utils.MainUtils;
import ee_man.mod3.utils.RobotMap;
import ee_man.mod3.utils.IUnloadListener;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetServerHandler;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

public class EntityRobot extends EntityPlayerMP implements IUnloadListener{
	
	public static boolean needRobotSleepWithAllPlayers = false;
	
	public static boolean writeDeathMes = false;
	
	public static String defName = "BioBot_%id";
	
	public static RobotMap robotLists = new RobotMap();
	
	public String ownerNick = null;
	
	public boolean isLocked = false;
	
	public int id = -1;
	
	public int[] tickersArray;
	
	public boolean[] boolArray;
	
	public EntityRobot(World par1World, int par2Id){
		super(MinecraftServer.getServer(), par1World, new String(defName).replaceAll("%id", Integer.toString(par2Id)), new ItemInWorldManager(par1World));
		id = par2Id;
		this.playerNetServerHandler = new NetServerHandler(MinecraftServer.getServer(), new FakeNetworkManager(), this);
		this.stepHeight = 0.5F;
		tickersArray = new int[3];
		for(int i = 0; i < tickersArray.length; i++){
			tickersArray[i] = 0;
		}
		
		boolArray = new boolean[7];
		for(int i = 0; i < boolArray.length; i++){
			boolArray[i] = false;
		}
	}
	
	public EntityRobot(World par1World){
		this(par1World, getNextId());
	}
	
	public void setOwner(String nick){
		if(ownerNick != null)
			robotLists.remove(ownerNick, this);
		ownerNick = nick;
		if(ownerNick != null)
			robotLists.addRobot(ownerNick, this);
	}
	
	public void onUnload(){
		if(ownerNick != null)
			robotLists.remove(ownerNick, this);
	}
	
	public MovingObjectPosition rayTraceBlock(double par1, float par3){
		Vec3 headVec = Vec3.createVectorHelper(this.posX, (this.posY + 1.62) - this.yOffset, this.posZ);
		Vec3 lookVec = this.getLook(1.0F);
		Vec3 endVec = headVec.addVector(lookVec.xCoord * par1, lookVec.yCoord * par1, lookVec.zCoord * par1);
		return this.worldObj.rayTraceBlocks_do_do(headVec, endVec, true, false);
	}
	
	public MovingObjectPosition rayTrace(double par2, float par1){
		MovingObjectPosition objectMouseOver = null;
		if(this != null){
			if(this.worldObj != null){
				Entity pointedEntity;
				double d0 = (double)par2;
				objectMouseOver = this.rayTraceBlock(d0, par1);
				double d1 = d0;
				Vec3 vec3 = Vec3.createVectorHelper(this.posX, (this.posY + 1.62) - this.yOffset, this.posZ);
				/*
				 * if(this.capabilities.isCreativeMode){ d0 = 6.0D; d1 = 6.0D; }
				 * else{ if(d0 > 3.0D){ d1 = 3.0D; }
				 * 
				 * d0 = d1; }
				 */
				
				if(objectMouseOver != null){
					d1 = objectMouseOver.hitVec.distanceTo(vec3);
				}
				
				Vec3 vec31 = this.getLook(par1);
				Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
				pointedEntity = null;
				float f1 = 1.0F;
				@SuppressWarnings("rawtypes")
				List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
				double d2 = d1;
				
				for(int i = 0; i < list.size(); ++i){
					Entity entity = (Entity)list.get(i);
					
					if(entity.canBeCollidedWith()){
						float f2 = entity.getCollisionBorderSize();
						AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
						MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
						
						if(axisalignedbb.isVecInside(vec3)){
							if(0.0D < d2 || d2 == 0.0D){
								pointedEntity = entity;
								d2 = 0.0D;
							}
						}
						else
							if(movingobjectposition != null){
								double d3 = vec3.distanceTo(movingobjectposition.hitVec);
								
								if(d3 < d2 || d2 == 0.0D){
									pointedEntity = entity;
									d2 = d3;
								}
							}
					}
				}
				
				if(pointedEntity != null && (d2 < d1 || objectMouseOver == null)){
					objectMouseOver = new MovingObjectPosition(pointedEntity);
				}
			}
		}
		return objectMouseOver;
	}
	
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("botId", id);
		par1NBTTagCompound.setString("ownerNick", ownerNick);
		for(int i = 0; i < boolArray.length; i++)
			par1NBTTagCompound.setBoolean("bool" + Integer.toString(i), boolArray[i]);
		for(int i = 0; i < tickersArray.length; i++)
			par1NBTTagCompound.setInteger("ticker" + Integer.toString(i), tickersArray[i]);
	}
	
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readEntityFromNBT(par1NBTTagCompound);
		ownerNick = par1NBTTagCompound.getString("ownerNick");
		if(!robotLists.hasRobot(ownerNick, this))
			robotLists.addRobot(ownerNick, this);
		for(int i = 0; i < boolArray.length; i++)
			boolArray[i] = par1NBTTagCompound.getBoolean("bool" + Integer.toString(i));
		for(int i = 0; i < tickersArray.length; i++)
			tickersArray[i] = par1NBTTagCompound.getInteger("ticker" + Integer.toString(i));
	}
	
	public boolean addEntityID(NBTTagCompound par1NBTTagCompound){
		String s = (String)EntityList.classToStringMapping.get(EntityRobotSaver.class);
		
		if(!this.isDead && s != null && this.riddenByEntity == null){
			par1NBTTagCompound.setString("id", s);
			this.writeToNBT(par1NBTTagCompound);
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean addNotRiddenEntityID(NBTTagCompound par1NBTTagCompound){
		String s = (String)EntityList.classToStringMapping.get(EntityRobotSaver.class);
		
		if(!this.isDead && s != null){
			par1NBTTagCompound.setString("id", s);
			this.writeToNBT(par1NBTTagCompound);
			return true;
		}
		else{
			return false;
		}
	}
	
	public void onUpdate(){
		for(int i = 0; i < tickersArray.length; i++){
			if(tickersArray[i] > 0)
				tickersArray[i]--;
		}
		
		if(this.boolArray[0] && !this.isUsingItem())
			this.rightClick();
		
		if(this.isHittingBlock)
			this.hittingBlockUpDate();
		else
			if(this.boolArray[1]){
				this.leftClick(false);
			}
		this.theItemInWorldManager.updateBlockRemoving();
		
		this.updateFlyingState(this.posY - this.prevPosY, this.onGround);
		
		super.onUpdateEntity();
	}
	
	public void updateEntityActionState(){
		super.updateEntityActionState();
		
		this.moveForward = 0.0F;
		this.moveStrafing = 0.0F;
		
		if(this.boolArray[2]){
			++this.moveForward;
		}
		
		if(this.boolArray[3]){
			--this.moveForward;
		}
		
		if(this.boolArray[4]){
			++this.moveStrafing;
		}
		
		if(this.boolArray[5]){
			--this.moveStrafing;
		}
		
		this.isJumping = this.boolArray[6];
		
		if(this.isSneaking()){
			this.moveStrafing = (float)((double)this.moveStrafing * 0.3D);
			this.moveForward = (float)((double)this.moveForward * 0.3D);
		}
		
		if(this.isUsingItem()){
			this.moveStrafing *= 0.2F;
			this.moveForward *= 0.2F;
		}
	}
	
	public boolean moveToBlockCenter(){
		if(this.tickersArray[2] <= 0){
			this.setPosition((int)this.posX + (this.posX >= 0 ? 0.5D : -0.5D), this.posY, (int)this.posZ + (this.posZ >= 0 ? 0.5D : -0.5D));
			this.tickersArray[2] = 40;
			return true;
		}
		else
			return false;
	}
	
	public void onUpdateEntity(){
	}
	
	@SuppressWarnings("rawtypes")
	public void sendContainerAndContentsToPlayer(Container par1Container, List par2List){
	}
	
	public void sendProgressBarUpdate(Container par1Container, int par2, int par3){
	}
	
	public void addChatMessage(String par1Str){
	}
	
	public String getPlayerIP(){
		return "bot";
	}
	
	public boolean canDespawn(){
		return false;
	}
	
	public boolean isPlayerSleeping(){
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		if(ste.getClassName().equals(WorldServer.class.getName()) && !needRobotSleepWithAllPlayers)
			return true;
		return this.sleeping;
	}
	
	public boolean isPlayerFullyAsleep(){
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		if(ste.getClassName().equals(WorldServer.class.getName()) && !needRobotSleepWithAllPlayers)
			return true;
		return this.sleeping && this.sleepTimer >= 100;
	}
	
	public void wakeUpPlayer(boolean par1, boolean par2, boolean par3){
		if(this.sleeping){
			super.wakeUpPlayer(par1, par2, par3);
		}
	}
	
	public void addVelocity(double par1, double par3, double par5){
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		if(ste.getClassName().equals(Entity.class.getName()))
			return;
		super.addVelocity(par1, par3, par5);
	}
	
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2){
		if(ForgeHooks.onLivingAttack(this, par1DamageSource, par2))
			return false;
		if(this.isEntityInvulnerable()){
			return false;
		}
		else
			if(this.worldObj.isRemote){
				return false;
			}
			else{
				this.entityAge = 0;
				
				if(this.func_110143_aJ() <= 0.0F){
					return false;
				}
				else
					if(par1DamageSource.isFireDamage() && this.isPotionActive(Potion.fireResistance)){
						return false;
					}
					else{
						if((par1DamageSource == DamageSource.anvil || par1DamageSource == DamageSource.fallingBlock) && this.getCurrentItemOrArmor(4) != null){
							this.getCurrentItemOrArmor(4).damageItem((int)(par2 * 4.0F + this.rand.nextFloat() * par2 * 2.0F), this);
							par2 *= 0.75F;
						}
						
						this.limbYaw = 1.5F;
						boolean flag = true;
						
						if((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F){
							if(par2 <= this.field_110153_bc){
								return false;
							}
							
							this.damageEntity(par1DamageSource, par2 - this.field_110153_bc);
							this.field_110153_bc = par2;
							flag = false;
						}
						else{
							this.field_110153_bc = par2;
							this.prevHealth = this.func_110143_aJ();
							this.hurtResistantTime = this.maxHurtResistantTime;
							this.damageEntity(par1DamageSource, par2);
							this.hurtTime = this.maxHurtTime = 10;
						}
						
						this.attackedAtYaw = 0.0F;
						Entity entity = par1DamageSource.getEntity();
						
						if(entity != null){
							if(entity instanceof EntityLivingBase){
								this.setRevengeTarget((EntityLivingBase)entity);
							}
							
							if(entity instanceof EntityPlayer){
								this.recentlyHit = 100;
								this.attackingPlayer = (EntityPlayer)entity;
							}
							else
								if(entity instanceof EntityWolf){
									EntityWolf entitywolf = (EntityWolf)entity;
									
									if(entitywolf.isTamed()){
										this.recentlyHit = 100;
										this.attackingPlayer = null;
									}
								}
						}
						
						if(flag){
							this.worldObj.setEntityState(this, (byte)2);
							
							if(par1DamageSource != DamageSource.drown){
								this.setBeenAttacked();
							}
							
							if(entity != null){
								double d0 = entity.posX - this.posX;
								double d1;
								
								for(d1 = entity.posZ - this.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D){
									d0 = (Math.random() - Math.random()) * 0.01D;
								}
								
								this.attackedAtYaw = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - this.rotationYaw;
								this.knockBack(entity, par2, d0, d1);
							}
							else{
								this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
							}
						}
						
						if(this.func_110143_aJ() <= 0.0F){
							if(flag){
								this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
							}
							
							this.onDeath(par1DamageSource);
						}
						else
							if(flag){
								this.playSound(this.getHurtSound(), this.getSoundVolume(), this.getSoundPitch());
							}
						
						return true;
					}
			}
	}
	
	public void onDeath(DamageSource par1DamageSource){
		if(ForgeHooks.onLivingDeath(this, par1DamageSource)){
			return;
		}
		
		if(writeDeathMes)
			this.mcServer.getConfigurationManager().sendChatMsg(this.func_110142_aN().func_94546_b());
		
		Entity entity = par1DamageSource.getEntity();
		
		if(entity != null){
			entity.onKillEntity(this);
		}
		
		this.dead = true;
		
		captureDrops = true;
		capturedDrops.clear();
		
		this.inventory.dropAllItems();
		
		captureDrops = false;
		PlayerDropsEvent event = new PlayerDropsEvent(this, par1DamageSource, capturedDrops, recentlyHit > 0);
		if(!MinecraftForge.EVENT_BUS.post(event)){
			for(EntityItem item : capturedDrops){
				joinEntityItemWithWorld(item);
			}
		}
	}
	
	public void onDeathUpdate(){
		super.onDeathUpdate();
		if(ownerNick != null){
			robotLists.remove(ownerNick, this);
			ownerNick = null;
		}
		if(this.deathTime >= 20)
			this.worldObj.removeEntity(this);
	}
	
	public boolean rightClick(){
		if(this.tickersArray[0] <= 0){
			MovingObjectPosition mop = this.rayTrace(4.5F, 1.0F);
			Mod3Event.EntityRobotEvent.OnRightClick event = Mod3Hooks.OnRightClickEntityRobot(mop, this);
			if(event.isCanceled())
				return false;
			if(event.mop == null && this.getHeldItem() != null){
				this.theItemInWorldManager.tryUseItem(this, this.worldObj, this.getHeldItem());
			}
			else
				if(event.mop != null && event.mop.typeOfHit == EnumMovingObjectType.TILE){
					float f = (float)event.mop.hitVec.xCoord - (float)event.mop.blockX;
					float f1 = (float)event.mop.hitVec.yCoord - (float)event.mop.blockY;
					float f2 = (float)event.mop.hitVec.zCoord - (float)event.mop.blockZ;
					if(this.theItemInWorldManager.activateBlockOrUseItem(this, this.worldObj, this.getHeldItem(), event.mop.blockX, event.mop.blockY, event.mop.blockZ, event.mop.sideHit, f, f1, f2))
						this.swingItem();
					else
						if(this.getHeldItem() != null)
							this.theItemInWorldManager.tryUseItem(this, this.worldObj, this.getHeldItem());
				}
				else
					if(event.mop != null && event.mop.typeOfHit == EnumMovingObjectType.ENTITY){
						if(!this.interactWith(event.mop.entityHit) && this.getHeldItem() != null)
							this.theItemInWorldManager.tryUseItem(this, this.worldObj, this.getHeldItem());
					}
			ItemStack itemstack = this.inventory.getCurrentItem();
			
			if(itemstack != null && itemstack.stackSize == 0){
				this.inventory.mainInventory[this.inventory.currentItem] = null;
				itemstack = null;
			}
			
			if(itemstack == null || itemstack.getMaxItemUseDuration() == 0){
				this.playerInventoryBeingManipulated = true;
				this.inventory.mainInventory[this.inventory.currentItem] = ItemStack.copyItemStack(this.inventory.mainInventory[this.inventory.currentItem]);
				this.playerInventoryBeingManipulated = false;
			}
			this.tickersArray[0] = 4;
			return true;
		}
		else
			return false;
	}
	
	public boolean leftClick(boolean can){
		if(this.tickersArray[1] <= 0){
			MovingObjectPosition mop = this.rayTrace(4.5F, 1.0F);
			Mod3Event.EntityRobotEvent.OnLeftClick event = Mod3Hooks.OnLeftClickEntityRobot(mop, this);
			if(event.isCanceled())
				return false;
			if(event.mop != null && event.mop.typeOfHit == EnumMovingObjectType.TILE){
				this.theItemInWorldManager.onBlockClicked(event.mop.blockX, event.mop.blockY, event.mop.blockZ, event.mop.sideHit);
				if(can)
					this.theItemInWorldManager.cancelDestroyingBlock(event.mop.blockX, event.mop.blockY, event.mop.blockZ);
				else{
					this.isHittingBlock = true;
					this.currentBlockX = event.mop.blockX;
					this.currentBlockY = event.mop.blockY;
					this.currentBlockZ = event.mop.blockZ;
					this.prevItemStack = this.getHeldItem();
					this.curBlockDamageMP = 0.0F;
					this.blockHitDelay = 0;
				}
			}
			else
				if(event.mop != null && event.mop.typeOfHit == EnumMovingObjectType.ENTITY){
					this.attackTargetEntityWithCurrentItem(event.mop.entityHit);
				}
			this.swingItem();
			ItemStack itemstack = this.inventory.getCurrentItem();
			
			if(itemstack != null && itemstack.stackSize == 0){
				this.inventory.mainInventory[this.inventory.currentItem] = null;
				itemstack = null;
			}
			
			if(itemstack == null || itemstack.getMaxItemUseDuration() == 0){
				this.playerInventoryBeingManipulated = true;
				this.inventory.mainInventory[this.inventory.currentItem] = ItemStack.copyItemStack(this.inventory.mainInventory[this.inventory.currentItem]);
				this.playerInventoryBeingManipulated = false;
			}
			this.tickersArray[1] = 10;
			return true;
		}
		else
			return false;
	}
	
	public boolean isHittingBlock = false;
	public int currentBlockX = -1;
	public int currentBlockY = -1;
	public int currentBlockZ = -1;
	public float curBlockDamageMP = 0.0F;
	public ItemStack prevItemStack = null;
	public int blockHitDelay = 0;
	
	public void hittingBlockUpDate(){
		if(this.blockHitDelay > 0){
			--this.blockHitDelay;
		}
		else{
			MovingObjectPosition mop = this.rayTrace(4.5F, 1.0F);
			if(mop != null && mop.typeOfHit == EnumMovingObjectType.TILE && this.sameToolAndBlock(mop.blockX, mop.blockY, mop.blockZ)){
				
				int i1 = this.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
				
				if(i1 == 0){
					this.isHittingBlock = false;
					return;
				}
				
				this.swingItem();
				
				Block block = Block.blocksList[i1];
				this.curBlockDamageMP += block.getPlayerRelativeBlockHardness(this, this.worldObj, mop.blockX, mop.blockY, mop.blockZ);
				
				if(this.curBlockDamageMP >= 1.0F){
					this.isHittingBlock = false;
					this.theItemInWorldManager.uncheckedTryHarvestBlock(mop.blockX, mop.blockY, mop.blockZ);
					this.curBlockDamageMP = 0.0F;
					this.blockHitDelay = 5;
				}
				
				// this.worldObj.destroyBlockInWorldPartially(this.entityId,
				// this.currentBlockX, this.currentBlockY,
				// this.currentBlockZ,
				// (int)(this.curBlockDamageMP * 10.0F) - 1);
			}
			
			else{
				this.theItemInWorldManager.cancelDestroyingBlock(currentBlockX, currentBlockY, currentBlockX);
				this.isHittingBlock = false;
			}
			
			this.tickersArray[1] = 10;
		}
		
	}
	
	private boolean sameToolAndBlock(int par1, int par2, int par3){
		ItemStack itemstack = this.getHeldItem();
		boolean flag = this.prevItemStack == null && itemstack == null;
		
		if(this.prevItemStack != null && itemstack != null){
			flag = itemstack.itemID == this.prevItemStack.itemID && ItemStack.areItemStackTagsEqual(itemstack, this.prevItemStack) && (itemstack.isItemStackDamageable() || itemstack.getItemDamage() == this.prevItemStack.getItemDamage());
		}
		
		return par1 == this.currentBlockX && par2 == this.currentBlockY && par3 == this.currentBlockZ && flag;
	}
	
	public void handle(int a, int b){
		Mod3Event.EntityRobotEvent.OnHandleEntityRobot event = Mod3Hooks.OnHandleEntityRobot(a, b, this);
		if(!event.isCanceled()){
			switch(event.a){
				case 0:{
					switch(event.b){
						case 0:{
							this.rightClick();
							if(this.isUsingItem())
								this.stopUsingItem();
						}
						break;
						case 1:{
							this.rightClick();
						}
						break;
						case 2:{
							this.boolArray[0] = true;
						}
						break;
						case 3:{
							this.boolArray[0] = false;
							this.stopUsingItem();
						}
						break;
						case 4:{
							this.leftClick(true);
						}
						break;
						case 5:{
							this.boolArray[1] = true;
						}
						break;
						case 6:{
							this.leftClick(false);
						}
						break;
						case 7:{
							this.isHittingBlock = false;
							this.boolArray[1] = false;
						}
						break;
					}
				}
				break;
				case 1:{
					switch(b){
						case 0:{
							this.dropOneItem(false);
						}
						break;
						case 1:{
							this.dropOneItem(true);
						}
						break;
						case 2:{
							
						}
						break;
						case 3:{
							
						}
						break;
						case 4:{
							
						}
						break;
						case 5:{
							this.rotationPitch = 0.0F;
						}
						break;
						case 6:{
							this.rotationYaw = 0.0F;
						}
						break;
						case 9:{
							this.attackEntityFrom(DamageSource.outOfWorld, 1000);
						}
						break;
					}
				}
				break;
				case 2:{
					if(event.b >= 0 && event.b < InventoryPlayer.getHotbarSize())
						this.inventory.currentItem = event.b;
				}
				break;
				case 3:{
					switch(event.b){
						case 0:{
							this.boolArray[2] = !this.boolArray[2];
						}
						break;
						case 1:{
							this.boolArray[3] = !this.boolArray[3];
						}
						break;
						case 2:{
							this.boolArray[4] = !this.boolArray[4];
						}
						break;
						case 3:{
							this.boolArray[5] = !this.boolArray[5];
						}
						break;
						case 4:{
							this.boolArray[6] = !this.boolArray[6];
						}
						break;
						case 5:{
							this.setSneaking(!this.isSneaking());
						}
						break;
						case 6:{
							this.setSprinting(!this.isSprinting());
						}
						break;
						case 7:{
							this.moveToBlockCenter();
						}
						break;
					}
				}
				break;
				case 4:{
					switch(event.b){
						case 0:{
							this.rotationPitch += 1;
						}
						break;
						case 1:{
							this.rotationPitch += 5;
						}
						break;
						case 2:{
							this.rotationPitch += 10;
						}
						break;
						case 3:{
							this.rotationPitch += 45;
						}
						break;
						case 4:{
							this.rotationPitch += 90;
						}
						break;
						case 5:{
							this.rotationPitch -= 1;
						}
						break;
						case 6:{
							this.rotationPitch -= 5;
						}
						break;
						case 7:{
							this.rotationPitch -= 10;
						}
						break;
						case 8:{
							this.rotationPitch -= 45;
						}
						break;
						case 9:{
							this.rotationPitch -= 90;
						}
						break;
					}
					if(this.rotationPitch < -90)
						this.rotationPitch = -90;
					if(this.rotationPitch > 90)
						this.rotationPitch = 90;
				}
				break;
				case 5:{
					switch(event.b){
						case 0:{
							this.rotationYaw += 1;
						}
						break;
						case 1:{
							this.rotationYaw += 5;
						}
						break;
						case 2:{
							this.rotationYaw += 10;
						}
						break;
						case 3:{
							this.rotationYaw += 45;
						}
						break;
						case 4:{
							this.rotationYaw += 90;
						}
						break;
						case 5:{
							this.rotationYaw -= 1;
						}
						break;
						case 6:{
							this.rotationYaw -= 5;
						}
						break;
						case 7:{
							this.rotationYaw -= 10;
						}
						break;
						case 8:{
							this.rotationYaw -= 45;
						}
						break;
						case 9:{
							this.rotationYaw -= 90;
						}
						break;
					}
					while(this.rotationYaw >= 360)
						this.rotationYaw -= 360;
					while(this.rotationYaw < 0)
						this.rotationYaw += 360;
				}
				break;
				case 6:{
					switch(event.b){
						case 0:{
							this.closeContainer();
						}
						break;
						case 1:{
							this.selectedSlot = 0;
						}
						break;
						case 2:{
							this.selectedSlot = this.openContainer.getInventory().size() - 1;
						}
						break;
						case 3:{
							this.selectedSlot++;
						}
						break;
						case 4:{
							this.selectedSlot--;
						}
						break;
						case 5:{
							if(this.selectedSlot >= 0 && this.selectedSlot < this.openContainer.getInventory().size() || this.selectedSlot == -999){
								this.openContainer.slotClick(this.selectedSlot, 0, 0, this);
							}
						}
						break;
						case 6:{
							if(this.selectedSlot >= 0 && this.selectedSlot < this.openContainer.getInventory().size() || this.selectedSlot == -999){
								this.openContainer.slotClick(this.selectedSlot, 1, 0, this);
							}
						}
						break;
						case 7:{
							if(this.selectedSlot >= 0 && this.selectedSlot < this.openContainer.getInventory().size() || this.selectedSlot == -999){
								this.openContainer.slotClick(this.selectedSlot, 0, 1, this);
							}
						}
						break;
						case 8:{
							if(this.selectedSlot >= 0 && this.selectedSlot < this.openContainer.getInventory().size() || this.selectedSlot == -999){
								this.openContainer.slotClick(this.selectedSlot, 1, 1, this);
							}
						}
						break;
						case 9:{
							this.selectedSlot = -999;
						}
						break;
					}
				}
				break;
			}
		}
	}
	
	public Object[] getInfo(int a, int b){
		Object[] toReturn = null;
		switch(a){
			case 0:{
				switch(b){
					case 0:{
						toReturn = new Object[]{Integer.valueOf(this.tickersArray[0])};
					}
					break;
					case 1:{
						toReturn = new Object[]{Boolean.valueOf(this.isUsingItem())};
					}
					break;
					case 2:{
						toReturn = new Object[]{Boolean.valueOf(this.boolArray[0])};
					}
					break;
					case 4:{
						toReturn = new Object[]{Integer.valueOf(this.tickersArray[1])};
					}
					break;
					case 5:{
						toReturn = new Object[]{Boolean.valueOf(this.isHittingBlock)};
					}
					break;
					case 6:{
						toReturn = new Object[]{Boolean.valueOf(this.boolArray[1])};
					}
					break;
				}
			}
			break;
			case 1:{
				switch(b){
					case 0:{
						toReturn = new Object[]{Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)};
					}
					break;
					case 1:{
						toReturn = new Object[]{this.worldObj.provider.getDimensionName(), Integer.valueOf(this.worldObj.provider.dimensionId)};
					}
					break;
					case 2:{
						
					}
					break;
					case 3:{
						MovingObjectPosition mop = this.rayTrace(4.5F, 1.0F);
						if(mop == null)
							toReturn = new Object[]{"null"};
						else{
							if(mop.typeOfHit.equals(EnumMovingObjectType.ENTITY)){
								toReturn = new Object[]{"entity", FMLDeobfuscatingRemapper.INSTANCE.map(mop.entityHit.getClass().getName().replace('.', '/')).replace('/', '.'), mop.entityHit.getEntityName(), Double.valueOf(mop.entityHit.posX), Double.valueOf(mop.entityHit.posY), Double.valueOf(mop.entityHit.posZ), Float.valueOf(mop.entityHit.rotationPitch), Float.valueOf(mop.entityHit.rotationYaw)};
							}
							else
								if(mop.typeOfHit.equals(EnumMovingObjectType.TILE)){
									toReturn = new Object[]{"block", Integer.valueOf(this.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ)), Integer.valueOf(this.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)), Integer.valueOf(mop.blockX), Integer.valueOf(mop.blockY), Integer.valueOf(mop.blockZ), Integer.valueOf(mop.sideHit)};
								}
						}
					}
					break;
					case 4:{
						MovingObjectPosition mop = this.rayTrace(45F, 1.0F);
						if(mop == null)
							toReturn = new Object[]{"null"};
						else{
							if(mop.typeOfHit.equals(EnumMovingObjectType.ENTITY)){
								toReturn = new Object[]{"entity", FMLDeobfuscatingRemapper.INSTANCE.map(mop.entityHit.getClass().getName().replace('.', '/')).replace('/', '.'), mop.entityHit.getEntityName(), Double.valueOf(mop.entityHit.posX), Double.valueOf(mop.entityHit.posY), Double.valueOf(mop.entityHit.posZ), Float.valueOf(mop.entityHit.rotationPitch), Float.valueOf(mop.entityHit.rotationYaw)};
							}
							else
								if(mop.typeOfHit.equals(EnumMovingObjectType.TILE)){
									toReturn = new Object[]{"block", Integer.valueOf(this.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ)), Integer.valueOf(this.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)), Integer.valueOf(mop.blockX), Integer.valueOf(mop.blockY), Integer.valueOf(mop.blockZ), Integer.valueOf(mop.sideHit)};
								}
						}
					}
					break;
					case 5:{
						toReturn = new Object[]{Float.valueOf(this.func_110139_bj())};
					}
					break;
					case 6:{
						toReturn = new Object[]{Float.valueOf(this.func_110143_aJ())};
					}
					break;
					case 7:{
						toReturn = new Object[]{Integer.valueOf(this.foodStats.getFoodLevel())};
					}
					break;
					case 8:{
						toReturn = new Object[]{Float.valueOf(this.experience)};
					}
					break;
					case 9:{
						toReturn = new Object[]{Integer.valueOf(this.experienceLevel)};
					}
					break;
				}
			}
			break;
			case 2:{
				switch(b){
					case 0:{
						toReturn = new Object[]{Integer.valueOf(this.inventory.currentItem + 1)};
					}
					break;
					case 1:{
						toReturn = MainUtils.getItemStackInfo(this.getHeldItem());
					}
					break;
				}
			}
			break;
			case 3:{
				switch(b){
					case 0:{
						toReturn = new Object[]{Boolean.valueOf(this.boolArray[2])};
					}
					break;
					case 1:{
						toReturn = new Object[]{Boolean.valueOf(this.boolArray[3])};
					}
					break;
					case 2:{
						toReturn = new Object[]{Boolean.valueOf(this.boolArray[4])};
					}
					break;
					case 3:{
						toReturn = new Object[]{Boolean.valueOf(this.boolArray[5])};
					}
					break;
					case 4:{
						toReturn = new Object[]{Boolean.valueOf(this.boolArray[6])};
					}
					break;
					case 5:{
						toReturn = new Object[]{Boolean.valueOf(this.isSneaking())};
					}
					break;
					case 6:{
						toReturn = new Object[]{Boolean.valueOf(this.isSprinting())};
					}
					break;
					case 7:{
						toReturn = new Object[]{Integer.valueOf(this.tickersArray[2])};
					}
					break;
				}
			}
			break;
			case 4:{
				switch(b){
					case 0:{
						toReturn = new Object[]{Float.valueOf(this.rotationPitch)};
					}
					break;
				}
			}
			break;
			case 5:{
				switch(b){
					case 0:{
						toReturn = new Object[]{Float.valueOf(this.rotationYaw)};
					}
					break;
				}
			}
			break;
			case 6:{
				switch(b){
					case 0:{
						toReturn = new Object[]{FMLDeobfuscatingRemapper.INSTANCE.map(this.openContainer.getClass().getName().replace('.', '/')).replace('/', '.')};
					}
					break;
					case 1:{
						toReturn = new Object[]{Integer.valueOf(this.selectedSlot)};
					}
					break;
					case 2:{
						toReturn = new Object[]{Integer.valueOf(this.openContainer.inventorySlots.size())};
					}
					break;
					case 4:{
						if(this.selectedSlot >= 0 && this.selectedSlot < this.openContainer.inventorySlots.size()){
							toReturn = MainUtils.getItemStackInfo(this.openContainer.getSlot(this.selectedSlot).getStack());
						}
						else
							toReturn = MainUtils.getItemStackInfo(null);
					}
					break;
					case 5:{
						toReturn = MainUtils.getItemStackInfo(this.inventory.getItemStack());
					}
					break;
				}
			}
			break;
		}
		Mod3Event.EntityRobotEvent.OnGetInfoEntityRobot event = Mod3Hooks.OnGetInfoEntityRobot(toReturn, a, b, this);
		return event.toReturn;
	}
	
	public int selectedSlot = 0;
	
	public static File nextIdFile;
	
	public static int getNextId(){
		if(nextIdFile == null){
			nextIdFile = new File(Core.proxy.getModFolderInWorld(), "bioBotNextId.txt");
		}
		int nextId = 0;
		
		try{
			if(!nextIdFile.exists()){
				nextIdFile.createNewFile();
			}
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(nextIdFile));
			try{
				nextId = Integer.parseInt(reader.readLine());
			} catch(NumberFormatException e){
				nextId = 0;
			}
			PrintWriter writer = new PrintWriter(new FileWriter(nextIdFile));
			writer.println(nextId + 1);
			writer.close();
		} catch(FileNotFoundException e){
		} catch(IOException e){
		}
		return nextId;
	}
}
