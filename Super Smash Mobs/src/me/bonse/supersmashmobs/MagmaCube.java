package me.bonse.supersmashmobs;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
	
public class MagmaCube implements Listener {

	private BukkitTask task;

	private BukkitTask task2;

	private BukkitTask task3;

	private Main main;

	public MagmaCube(Main main) {

		this.main = main;
	}

	ArrayList<UUID> cooldown = new ArrayList<UUID>();
	ArrayList<UUID> flamedash = new ArrayList<UUID>();
	ArrayList<UUID> cooldown2 = new ArrayList<UUID>();
	

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();

		Action action = event.getAction();

		UUID uuid = player.getUniqueId();

		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			
			ItemStack shovel = new ItemStack(Material.IRON_SPADE);
			ItemStack axe = new ItemStack(Material.IRON_AXE);

			ItemMeta ims = shovel.getItemMeta();
			ItemMeta ima = axe.getItemMeta();

			ims.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Flame Dash - Right Click");
			ima.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Magma Blast - Right Click");

			shovel.setItemMeta(ims);
			axe.setItemMeta(ima);
			

			if (player.getInventory().getItemInMainHand().equals(shovel)) {
				
				if (cooldown.contains(uuid)) {

					if (!(flamedash.contains(uuid))) {

						player.sendMessage(ChatColor.RED + "You need to wait before using Flame Dash again!");

					}
					
				}

				else {

					flamedash.add(uuid);
					cooldown.add(uuid);
					
					player.setCooldown(Material.IRON_SPADE, 140);

					for (Player p : Bukkit.getServer().getOnlinePlayers()) {

						p.hidePlayer(player);
					}										

					player.setAllowFlight(true);
					player.setFlying(true);
					player.setFlySpeed((float) 0.1);
					player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_BURN, 10, 1);
					
					new BukkitRunnable() {
						
						@Override
						public void run() {
							
							if (!(flamedash.contains(uuid))) {
								
								this.cancel();
							}
							
							if (!(player.isSneaking())) {
								
								player.setVelocity(player.getLocation().getDirection().multiply(0.7));
							}
							
							if (player.isSneaking()) {
								
								player.setFlying(false);
							}
							
							else {
								
								player.setFlying(true);
							}
								
						}
						
					}.runTaskTimer(main, 0, 0);

					player.sendMessage(
							ChatColor.GREEN + "You used " + ChatColor.RED + "" + ChatColor.BOLD + "Flame Dash");

					new BukkitRunnable() {

						@Override
						public void run() {

							if (player.isDead()) {

								this.cancel();

							}

							player.setAllowFlight(false);
							player.setFlying(false);

							for (Player p : Bukkit.getServer().getOnlinePlayers()) {

								p.showPlayer(player);
							}

							World world = player.getWorld();
							int x = player.getLocation().getBlockX();
							int y = player.getLocation().getBlockY();
							int z = player.getLocation().getBlockZ();
							world.createExplosion(x, y, z, 3, false, false);

							flamedash.remove(uuid);

							player.sendMessage(
									ChatColor.RED + "" + ChatColor.BOLD + "Flame Dash " + ChatColor.GREEN + "ended!");

						}

					}.runTaskLater(main, 25);

					task = new BukkitRunnable() {

						@Override
						public void run() {

							cooldown.remove(uuid);

							player.sendMessage(ChatColor.GREEN + "You can use " + ChatColor.RED + "" + ChatColor.BOLD
									+ "Flame dash" + ChatColor.GREEN + " again!");

						}

					}.runTaskLater(main, 140);

				}

				if (flamedash.contains(uuid)) {

					int air = player.getRemainingAir();
					int hunger = player.getFoodLevel();

					new BukkitRunnable() {

						@Override
						public void run() {

							player.setFoodLevel(hunger);
							player.setFireTicks(0);
							for (PotionEffect effect : player.getActivePotionEffects())
								player.removePotionEffect(effect.getType());

							if (!(flamedash.contains(uuid))) {

								this.cancel();
							}
						}

					}.runTaskTimer(main, 0, 0);

					task3 = new BukkitRunnable() {

						@Override
						public void run() {

							player.setRemainingAir(air);

							if (!(flamedash.contains(uuid))) {

								this.cancel();
							}

						}

					}.runTaskTimer(main, 0, 0);

					new BukkitRunnable() {

						@Override
						public void run() {

							Location l = player.getLocation();

							PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(EnumParticle.FLAME,
									true, (float) l.getX(), (float) l.getY(), (float) l.getZ(), 255, 0, 0, 0, 0, 1);
							((CraftPlayer) player).getHandle().playerConnection.sendPacket(particles);

							if (!(flamedash.contains(uuid))) {

								this.cancel();
							}

						}

					}.runTaskTimerAsynchronously(main, 0, 3);

				}

			}

			if (player.getInventory().getItemInMainHand().equals(axe)) {

				if (mbcooldown.contains(player)) {

					player.sendMessage(ChatColor.RED + "You need to wait before using Magma Blast again!");
				}

				else {
					
					player.setCooldown(Material.IRON_AXE, 120);

					Fireball f = player.launchProjectile(Fireball.class);

					f.setIsIncendiary(false);
					f.setYield(2);
					f.setShooter(player);

					Vector v = player.getEyeLocation().getDirection().multiply(1.8);

					new BukkitRunnable() {

						@Override
						public void run() {

							f.setVelocity(v);

							if (f.isDead()) {

								this.cancel();
							}

						}

					}.runTaskTimer(main, 0, 0);

					if (!(flamedash.contains(player))) {

						player.setVelocity(player.getLocation().getDirection().multiply(-1.3));

					}

					mbcooldown.add(player);

					player.sendMessage(
							ChatColor.GREEN + "You used " + ChatColor.RED + "" + ChatColor.BOLD + "Magma Blast");

					task2 = new BukkitRunnable() {

						@Override
						public void run() {

							player.sendMessage(ChatColor.GREEN + "You can use " + ChatColor.RED + "" + ChatColor.BOLD
									+ "Magma Blast");

							mbcooldown.remove(uuid);
						}

					}.runTaskLater(main, 120);

				}

			}

		}

	}

	@EventHandler
	public void damage(EntityDamageEvent event) {

		event.getCause();

		if (event.getCause().equals(DamageCause.FALL)) {

			event.setCancelled(true);

		}

		if (event.getEntity() instanceof Player) {

			Player player = (Player) event.getEntity();

			UUID uuid = player.getUniqueId();

			if (flamedash.contains(uuid)) {

				event.setCancelled(true);

			}

		}
		
	}

	@EventHandler
	public void death(PlayerDeathEvent event) {

		UUID uuid = event.getEntity().getUniqueId();

		task.cancel();
		task2.cancel();

		flamedash.remove(uuid);
		cooldown.remove(uuid);
		cooldown2.remove(uuid);

	}

	@EventHandler
	public void inventoryItem(InventoryClickEvent event) {
		
		ItemStack shovel = new ItemStack(Material.IRON_SPADE);
		ItemStack axe = new ItemStack(Material.IRON_AXE);
		ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
		ItemStack chestPlate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);

		ItemMeta ims = shovel.getItemMeta();
		ItemMeta ima = axe.getItemMeta();

		ims.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Flame Dash - Right Click");
		ima.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Magma Blast - Right Click");

		shovel.setItemMeta(ims);
		axe.setItemMeta(ima);

		ItemStack item = event.getCurrentItem();

		if (item.equals(shovel) || item.equals(axe) || item.equals(helmet) || item.equals(chestPlate) || item.equals(boots)) {

			event.setCancelled(true);
		}

	}

	@EventHandler
	public void move(PlayerMoveEvent event) {

		Player player = event.getPlayer();

		UUID uuid = event.getPlayer().getUniqueId();

		Location l = player.getLocation();
		l.setY(l.getY() + 1.7);

		Block b = l.getBlock();

		if (flamedash.contains(uuid)) {

			if (!(b.isLiquid())) {

				task3.cancel();

				new BukkitRunnable() {

					@Override
					public void run() {

						player.setRemainingAir(300);

						if (!(flamedash.contains(uuid))) {

							this.cancel();
						}
					}

				}.runTaskTimer(main, 0, 0);

			}
			

		}

	}
		
}
