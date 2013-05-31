package de.bplaced.mopfsoft;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.block.Action;

public class MinecraftPlayerListener implements Listener {

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		if (RandomInventory.worldMap.get(event.getPlayer().getWorld()) != null && RandomInventory.worldMap.get(event.getPlayer().getWorld())) {
			RandomInventory.setPlaying(event.getPlayer(), true);
		}

	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {

		RandomInventory.setPlaying(event.getPlayer(),
				RandomInventory.worldMap.get(event.getPlayer().getWorld()));

	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {

		RandomInventory.setPlaying(event.getPlayer(), false);

	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {

		if (RandomInventory.isPlaying(event.getPlayer())) {
			event.setCancelled(true);
		}

	}
	
	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event) {

		if (RandomInventory.isPlaying(event.getPlayer())) {
			if (!RandomInventory.canFitIn(event.getPlayer().getInventory(), event.getItem().getItemStack())) {
				event.setCancelled(true);
			}
		}

	}
	
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent event) {

		if (RandomInventory.isPlaying((Player)event.getWhoClicked())) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {

		// Check if player right clicked with diamond pickaxe and is playing
		if (RandomInventory.isPlaying(event.getPlayer())
				&& (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event
						.getAction().equals(Action.RIGHT_CLICK_AIR))
						&& event.getPlayer().getItemInHand().getType()
								.equals(Material.DIAMOND_PICKAXE)) {

			RandomInventory.tryToGiveNewInventory(event.getPlayer());

		}

	}
}
