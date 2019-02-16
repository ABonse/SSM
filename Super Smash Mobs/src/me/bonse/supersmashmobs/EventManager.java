package me.bonse.supersmashmobs;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.weather.WeatherChangeEvent;


public class EventManager implements Listener {
	
	@EventHandler
	public void swapItems(PlayerSwapHandItemsEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void itemPickUp(EntityPickupItemEvent event) {
		
		if (event.getEntity() instanceof Player) {
			
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void dropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);

	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void itemDamage(PlayerItemDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {
		
		event.setKeepInventory(true);
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		player.getInventory().clear();
		
		for (Entity e : player.getWorld().getEntities()) {
			
			e.setGlowing(false);
		}
		
	}

	@EventHandler
	public void flight(PlayerToggleFlightEvent event) {
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void weather(WeatherChangeEvent event) {
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void sneak(PlayerToggleSneakEvent event) {
		
		event.setCancelled(true);
	}
		
}


