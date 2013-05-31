package de.bplaced.mopfsoft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomInventory extends JavaPlugin{
	
	public static Random randomGen = new Random();
	public static List<String> itemList = new ArrayList<String>();
	
	public static HashMap <Player, Boolean> playerMap = new HashMap<Player,Boolean>();
	public static HashMap <Player, Long> lastInteractionMap = new HashMap<Player,Long>();
	public static HashMap <World, Boolean> worldMap = new HashMap<World,Boolean>();
	
	public static int DELAY_BETWEEN_INVENTORIES =  30; //in seconds 
	
	 @Override
	    public void onEnable(){
		 
		 //load config
		 saveDefaultConfig();
		 
		 for (String world: getConfig().getString("worlds").split(",")) {
			 System.out.println("Starting RandomInventory in world: "+world);
			 setWorld(getServer().getWorld(world), true);
		 }
		 
		 DELAY_BETWEEN_INVENTORIES = getConfig().getInt("delay");
		 
		 
		 
		 //load item list
			try {
				
				File file = new File("plugins"+System.getProperty("file.separator")+"RandomInventory"+System.getProperty("file.separator")+"values.txt");
				if (!file.exists()) {
					new File("plugins"+System.getProperty("file.separator")+"RandomInventory").mkdirs();
					file.createNewFile();
					
					InputStream is = this.getClass().getResourceAsStream(
							"/resources/values.txt");
					FileOutputStream os = new FileOutputStream(file);
					
					for (int read = 0; (read = is.read()) != -1;) {
						os.write(read);
					}
					os.flush();
					os.close();
				}
				
				
			FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);
			
			String line;
			while ((line=reader.readLine()) != null) {
				if ((new ItemStack(Integer.parseInt(line.split(":")[0]),1 ,Short.parseShort(line.split(":")[1]))).getType().equals(Material.AIR)) {
					System.out.println("Could not find block "+line+" in Minecraft... Ignoring");
				} else
				itemList.add(line);
			}
			
			reader.close();
			fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		 
		 
		 getServer().getPluginManager().registerEvents(new MinecraftBlockListener(), this);
		 getServer().getPluginManager().registerEvents(new MinecraftPlayerListener(), this);
		 
		 getCommand("randominv").setExecutor(new MinecraftCommandListener());
		 
		 System.out.println("Activated RandomInventory");
	 }
	 
	 public static void setWorld(World world, boolean isPlaying) {
		 worldMap.put(world, isPlaying);
		 for (Player p: world.getPlayers()) {
			 setPlaying(p, isPlaying);
		 }
	}

	@Override
	    public void onDisable(){
		for (Entry <Player,Boolean> e: playerMap.entrySet()) {
			if (e.getValue() == true) {
				setPlaying(e.getKey(), false);
			}
		}
		
		 System.out.println("Deactivated RandomInventory");
	 }
	 
	 
	 @SuppressWarnings("deprecation")
	public static void giveNewInventory(Player player) {
		// Set delay
		RandomInventory.lastInteractionMap.put(player,
				System.currentTimeMillis());
		
		//Fill hungerbar
		player.setFoodLevel(20);
		 
		 
			for (int i = 1; i<9; i++) {
				//type:value
				String[] randomItem = itemList.get(randomGen.nextInt(itemList.size())).split(":");

				int max;
				if (randomItem.length >= 3) {
					max = Math.max(1, Integer.parseInt(randomItem[2]));
				} else {
					max = 63;
				}
				
				int amount = (1+randomGen.nextInt(max));
				
				ItemStack stack = new ItemStack(Integer.parseInt(randomItem[0]),amount ,Short.parseShort(randomItem[1]));
			
				
				player.getInventory().setItem(i, stack);
				

			}
			
			//Give pickaxe
			ItemStack tool = new ItemStack(278,1,(short)0);
			
			tool.addEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
			tool.addEnchantment(Enchantment.SILK_TOUCH, Enchantment.SILK_TOUCH.getMaxLevel());
			tool.addEnchantment(Enchantment.DIG_SPEED, Enchantment.DIG_SPEED.getMaxLevel());
			
			player.getInventory().setItem(0, tool);
			
			try {
			player.updateInventory();
			} catch (Exception e) {
				
			}
	 }
	 
	public static void setPlaying(Player p, boolean isPlaying) {
		playerMap.put(p, isPlaying);
		if (isPlaying)
		tryToGiveNewInventory(p);
	 }
	
	public static boolean tryToGiveNewInventory(Player p) {
		// Check for delay between inventory refreshes
		if (RandomInventory.lastInteractionMap.get(p) == null
				|| System.currentTimeMillis()
						- RandomInventory.lastInteractionMap.get(p) >= RandomInventory.DELAY_BETWEEN_INVENTORIES * 1000) {
			p.sendMessage("Swapped Inventory!");
			RandomInventory.giveNewInventory(p);
			return true;
			
		} else {
			p.sendMessage("Please wait for "
					+ ((RandomInventory.DELAY_BETWEEN_INVENTORIES)-(System.currentTimeMillis() - RandomInventory.lastInteractionMap
							.get(p)) / 1000) + " seconds to swap your inventory again...");
			return false;
		}
	}

	public static boolean isPlaying(Player p) {
		return (playerMap.get(p) != null && playerMap.get(p));
	}

	public static boolean canFitIn(PlayerInventory inv,
			ItemStack stack) {
		for (int i = 0; i<9; i++) {
			if (inv.getItem(i) == null || inv.getItem(i).getTypeId() == 0) {
				return true;
			} else
			if (inv.getItem(i).getType().equals(stack.getType()) && inv.getItem(i).getAmount()+stack.getAmount()<=stack.getType().getMaxStackSize()) {
				return true;
			}
		}
		return false;
	}
}
