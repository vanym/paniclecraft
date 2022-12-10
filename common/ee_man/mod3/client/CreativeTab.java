package ee_man.mod3.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ee_man.mod3.Core;
import ee_man.mod3.DefaultProperties;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs{
	
	public ItemStack item;
	
	public CreativeTab(String label){
		super(label);
	}
	
	public ItemStack getIconItemStack(){
		if(item == null){
			ArrayList<ItemStack> items = Core.getAllItemsArray();
			Random rand = new Random();
			if(!items.isEmpty())
				item = items.get(Math.abs(rand.nextInt() % items.size()));
			else
				item = null;
		}
		return item;
	}
	
	public String getTranslatedTabLabel(){
		return DefaultProperties.MOD_NAME;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void displayAllReleventItems(List par1List){
		Iterator items = Core.getAllItemsArray().iterator();
		while(items.hasNext()){
			par1List.add(items.next());
		}
	}
}
