package org.styllex.clipboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.*;
import org.styllex.clipboard.listener.IC3D_Listener_10_90_13;

public class Mega extends JavaPlugin{
	
	private Configuration conf;
	private HashMap<Block, Player> clip = new HashMap<Block, Player>();
	private File file = new File("plugins/Clipboard/config.yml");
	
	private IC3D_Listener_10_90_13 pl = new IC3D_Listener_10_90_13(this);
	
	private boolean enabled=true;
	private int tool_id= -1;
	private String tool_name="stick";
	private boolean per_person=true;
	
	@Override
	public void onDisable(){
		System.out.println("Clipboard has been disabled");
		getServer().broadcastMessage("Clipboard has been disabled");
	}
	@Override
	public void onEnable(){
		
		//<!-- CONFIGURATION HANDELING -->
		
		boolean pass = true;
		boolean init=false;
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			try{
				file.createNewFile();
				init=true;
			}catch (IOException e){
				pass=false;
				System.out.println("creation of configuration file failed - Clipboard");
			}
		}
		if(pass){
			conf=new Configuration(file);
			if(init){
				System.out.println("Initalizing config file");
				
				/*-init-*/conf.load();
				/*-init-*/conf.setProperty("set-enabled", "true");
				/*-init-*/conf.setProperty("selection-item-id", "~");
				/*-init-*/conf.setProperty("selection-item-name", "stick");
				/*-init-*/conf.setProperty("clip-per-person", "true");
				/*-init-*/conf.save();
				
				System.out.println("Config file initalized");
			}else{
				enabled=toBool((String)conf.getProperty("set-enabled"));
				tool_id=Integer.parseInt((String)conf.getProperty("selection-item-id"));
				tool_name=(String)conf.getProperty("selection-item-name");
				per_person=toBool((String)conf.getProperty("clip-per-person"));
			}
			if(!enabled){
				getServer().broadcastMessage("cb disabled...");
				System.out.println("Clipboard - Config File Set To Disabled - Reversing Enabling Process");
				//this.setEnabled(false);
			}
		}
		
		//<!-- END OF CONFIGURATION HANDELING -->
		
		System.out.println("Clipboards Enabled");
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.PLAYER_INTERACT, pl, Event.Priority.Normal, this);
	}
	
	public Block[] hashToArray(HashMap<Block, Player> hash){
		Set<Block> hset=hash.keySet();
		Object[] obarray = hset.toArray();
		Block[] barray=new Block[hset.size()];
		for(Object ob : obarray){
			barray[barray.length]=(Block)ob;
		}
		return barray;
	}
	public boolean toBool(String s){
		if(s.equalsIgnoreCase("true")){
			return true;
		}
		return false;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("clip")||cmd.getName().equalsIgnoreCase("clipboard")||cmd.getName().equalsIgnoreCase("clipboards")||cmd.getName().equalsIgnoreCase("cb")){
			if(args.length==0){
				if(sender instanceof Player){
					sender.sendMessage("/"+cmd.getName()+" {wall} {height} // will copy the shape upward the specified layers to make a wall");
					sender.sendMessage("/"+cmd.getName()+" {shift} {x_amount} {y_amount} {z_amount} // move the selected over some units");
					sender.sendMessage("/"+cmd.getName()+" {pan} {x_amount} {z_amount} // move the selected along a plane");
					sender.sendMessage("/"+cmd.getName()+" {paste} // this will past from where you are standing as first block");
					sender.sendMessage("/"+cmd.getName()+" {clear} // clear your clipboard");
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("wall")){
				if(args.length==1){
					sender.sendMessage("/"+cmd.getName()+" wall {height}");
					sender.sendMessage("Takes the selected and extends it");
					sender.sendMessage("up the number on layers you input.");
					sender.sendMessage("This is for making walls and other");
					sender.sendMessage("high structures");
					return true;
				}
				int height = Integer.parseInt(args[1]);
				Block[] bl = hashToArray(clip);
				World world = ((Player)sender).getWorld();
				for(int i=0;i<height;i++){
					for(Block b : bl){
						if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
							Location loc = b.getLocation();
							loc.setY(loc.getY()+i);
							Block block=world.getBlockAt(loc);
							block.setType(b.getType());
						}
					}
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("shift")){
				if(args.length<4){
					sender.sendMessage("/"+cmd.getName()+" {shift} {x_amount} {y_amount} {z_amount}");
					sender.sendMessage("This will move everything selected over the selected units");
					sender.sendMessage("and will fill the original places with air");
					return true;
				}
				int xa = Integer.parseInt(args[1]);
				int ya = Integer.parseInt(args[2]);
				int za = Integer.parseInt(args[3]);
				Block[] bl = hashToArray(clip);
				World world = ((Player)sender).getWorld();
				for(Block b : bl){
					if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
						Location loc = b.getLocation();
						loc.setX(loc.getX()+xa);
						loc.setY(loc.getY()+ya);
						loc.setZ(loc.getZ()+za);
						Block block=world.getBlockAt(loc);
						block.setType(b.getType());
						b.setType(Material.AIR);
						Player player = clip.get(b);
						clip.remove(b);
						clip.put(block, player);
					}
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("pan")){
				if(args.length<3){
					sender.sendMessage("/"+cmd.getName()+" {pan} {x_amount} {z_amount}");
					sender.sendMessage("Same as shift only with locked height");
					return true;
				}
				int xa = Integer.parseInt(args[1]);
				int za = Integer.parseInt(args[2]);
				Block[] bl = hashToArray(clip);
				World world = ((Player)sender).getWorld();
				for(Block b : bl){
					if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
						Location loc = b.getLocation();
						loc.setX(loc.getX()+xa);
						loc.setZ(loc.getZ()+za);
						Block block=world.getBlockAt(loc);
						block.setType(b.getType());
						b.setType(Material.AIR);
						Player player = clip.get(b);
						clip.remove(b);
						clip.put(block, player);
					}
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("clear")){
				Block[] bl = hashToArray(clip);
				for(Block b : bl){
					if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
						clip.remove(b);
					}
				}
				return true;
			}
		}
		return false;
	}
	public Material getSelectionTool(){
		Material mat = Material.valueOf(tool_name.toUpperCase());
		if(mat==null){
			mat = Material.STICK;
		}
		return mat;
	}
	
	/**
	 * Adds or removes a block with the value of the player who clicked it to the clipboard
	 * 
	 * @param block : the block clicked
	 * @param player : the player who clicked
	 * @return a boolean - false if block was removed and true if it was added
	 */
	public boolean addClip(Block block, Player player){
		if(clip.containsKey(block)){
			if(clip.get(block).getDisplayName().equalsIgnoreCase(player.getDisplayName())){
				clip.remove(block);
				player.sendMessage("Block Removed From Clipboard");
				return false;
			}else{
				player.sendMessage("Someone Else Has Selected This Block");
				return false;
			}
		}else{
			clip.put(block, player);
			return true;
		}
	}
}