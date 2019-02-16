package me.bonse.supersmashmobs;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {

		getServer().getPluginManager().registerEvents(new EventManager(), this);
		getServer().getPluginManager().registerEvents(new MagmaCube(this), this);
		
	}

	@Override
	public void onDisable() {

	}
	
	ArrayList<Player> reveal = new ArrayList<Player>();
	
	private BukkitTask task;
	
	MagmaCube magmacube = new MagmaCube(this);

	public void kit(Player player) {

		player.sendMessage(ChatColor.GREEN + "You selected the Magma Cube kit!");
		
		MobDisguise magma = new MobDisguise(DisguiseType.MAGMA_CUBE);
		
		SlimeWatcher w = new SlimeWatcher(magma);
		
		magma.setWatcher(w);
		
		w.setSize(2);
		
		magma.setViewSelfDisguise(false);
		
		DisguiseAPI.disguiseEntity(player, magma);
		

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

		player.getInventory().clear();
		player.getInventory().addItem(shovel);
		player.getInventory().addItem(axe);
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestPlate);
		player.getInventory().setBoots(boots);

		task = new BukkitRunnable() {

			@Override
			public void run() {

				Location l = player.getLocation();

				PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(EnumParticle.LAVA, true,
						(float) l.getX(), (float) l.getY(), (float) l.getZ(), 0, 0, 0, 0, 0, 1);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(particles);
			}
		}.runTaskTimer(this, 0, 10);
	}
	

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (cmd.getName().equalsIgnoreCase("kit")) {

			if (!(sender instanceof Player)) {

				sender.sendMessage(ChatColor.RED + "Only Players may use this command!");

				return false;
			}

			if (args.length == 0) {

				sender.sendMessage(ChatColor.RED + "Specify a kit name!");

				return false;
			}

			if (args.length > 1) {

				sender.sendMessage(ChatColor.RED + "Too many arguments!");

				return false;

			}

			if (!(args[0].equalsIgnoreCase("magmacube"))) {
				
				sender.sendMessage(ChatColor.RED + args[0] + " is not a kit!");
				
				return false;

			}
			
			else {
				
				Player player = (Player) sender;

				kit(player);

				return true;
			}

		}
		
		if (cmd.getName().equalsIgnoreCase("reveal")) {
			
			if (!(sender instanceof Player)) {
				
				sender.sendMessage(ChatColor.RED + "Only players may use this command!");
				
				return false;
			}
			
			Player player = (Player) sender;
			
			if (!(reveal.contains(player))) {
				
				reveal.add(player);
				
				for (Entity e : player.getWorld().getEntities()) {
					
					e.setGlowing(true);
				}
					
				player.sendMessage(ChatColor.GREEN + "Entities Revealed");
				
				return true;
			}
			
			else {
				
				reveal.remove(player);
				
				for (Entity e : player.getWorld().getEntities()) {
					
					e.setGlowing(false);
				}
				
				player.sendMessage(ChatColor.RED + "Entities Hidden");
				
				return true;
				
			}
			
			
		}
		
		if (cmd.getName().equalsIgnoreCase("remove")) {
			
			if (!(sender instanceof Player)) {
				
				sender.sendMessage(ChatColor.RED + "Only player may use this command!");
				
				return false;
			}
			
			else {
				
				Player player = (Player) sender;
				
				player.getInventory().clear();
				
				task.cancel();
				
				return true;
			}
		}

		if (cmd.getName().equalsIgnoreCase("getlocation")) {

			if (!(sender instanceof Player)) {

				sender.sendMessage(ChatColor.RED + "Only players may use this command!");

				return false;
			}

			Player player = (Player) sender;
			int x = player.getLocation().getBlockX();
			int y = player.getLocation().getBlockY();
			int z = player.getLocation().getBlockZ();
			player.sendMessage(ChatColor.GREEN + "Your location is " + ChatColor.RED + x + "X " + y + "Y " + z + "Z");

			return true;

		}

		else if (cmd.getName().equalsIgnoreCase("heal")) {

			if (!(sender instanceof Player)) {

				sender.sendMessage(ChatColor.RED + "Only players may use this command!");

				return false;
			}
			Player player = (Player) sender;
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setFireTicks(0);

			for (PotionEffect effect : player.getActivePotionEffects())
				player.removePotionEffect(effect.getType());

			player.setRemainingAir(300);

			player.sendMessage(ChatColor.GREEN + "You have been healed!");				

			return true;
		}

		return false;

	}

}
