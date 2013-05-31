package de.bplaced.mopfsoft;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.PlayerInventory;


public class MinecraftBlockListener implements Listener{
	

	public MinecraftBlockListener() {

	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (RandomInventory.isPlaying(event.getPlayer())) {
			if (!hasOneItemLeft(event.getPlayer().getInventory())) {
				RandomInventory.giveNewInventory(event.getPlayer());
			}
		}
	}
	

	private boolean hasOneItemLeft(PlayerInventory inv) {
		boolean foundOne = false;
		for (int i = 0; i <= inv.getSize(); i++) {

			if (inv.getItem(i) != null && inv.getItem(i).getTypeId() != 0 && inv.getItem(i).getTypeId() != 278) {
				if (inv.getItem(i).getAmount() == 1 && !foundOne) {
					foundOne = true;
				} else
					return true;
			}
		}
		return false;
	}

}
