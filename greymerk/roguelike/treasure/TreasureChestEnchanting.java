package greymerk.roguelike.treasure;

import greymerk.roguelike.catacomb.Catacomb;
import greymerk.roguelike.catacomb.dungeon.Dungeon;
import greymerk.roguelike.treasure.loot.Loot;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntityChest;

public class TreasureChestEnchanting extends TreasureChestBase{
	
	@Override
	protected void fillChest(TileEntityChest chest){
		int level = Catacomb.getLevel(posY);
		int middle = chest.getSizeInventory()/2;
				
		ItemStack item;
		
		item = new ItemStack(Item.expBottle, 2 + rand.nextInt(6));
		chest.setInventorySlotContents(middle - 1, item);
		
		item = Loot.getEnchantedBook(rand, level);
		chest.setInventorySlotContents(middle, item);
		
		item = new ItemStack(Item.enderPearl, 1 + rand.nextInt(3));
		chest.setInventorySlotContents(middle + 1, item);
	}
	

}