package org.styllex.clipboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.styllex.clipboard.listener.IC3D_Listener_10_90_13;

public class Mega extends JavaPlugin{
	
	private Configuration conf;
	private HashMap<Block, Player> clip = new HashMap<Block, Player>();
	
	private File file = new File("plugins/Clipboard/config.yml");
	
	private IC3D_Listener_10_90_13 pl = new IC3D_Listener_10_90_13(this);
	
	private boolean enabled=true;
	private int tool_id= -1;
	private String tool_name="stick";
	private String cube_tool_name="string";
	private int cube_tool_id=-1;
	private HashMap<Player, Location> c1 = new HashMap<Player, Location>();
	private HashMap<Player, Location> c2 = new HashMap<Player, Location>();
	public HashMap<Player, Integer> c3 = new HashMap<Player, Integer>();
	
	private String lcommand;
	
	@Override
	public void onDisable(){
		conf.save();
		System.out.println("Clipboard has been disabled");
		getServer().broadcastMessage("Clipboard has been disabled");
	}
	@Override
	public void onEnable(){
		
		lcommand="clipboard";
		
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
				/*-init-*/conf.setProperty("selection-item-id", "-1");
				/*-init-*/conf.setProperty("selection-item-name", "stick");
				/*-init-*/conf.setProperty("cube-selection-item-name", "string");
				/*-init-*/conf.setProperty("cube-selection-item-id", "-1");
				/*-init-*/conf.save();
				
				System.out.println("Config file initalized");
			}else{
				conf.load();
				enabled=toBool((String)conf.getProperty("set-enabled"));
				tool_id=Integer.parseInt((String)conf.getProperty("selection-item-id"));
				tool_name=(String)conf.getProperty("selection-item-name");
				cube_tool_name=(String)conf.getProperty("cube-selection-item-name");
				cube_tool_id=Integer.parseInt((String)conf.getProperty("cube-selection-item-id"));
				conf.save();
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
		int i=0;
		for(Object ob : obarray){
			barray[i]=(Block)ob;
			i++;
		}
		return barray;
	}
	public Object[][] HashToArray(HashMap<Block, Material> hash){
		Set<Block> hset=hash.keySet();
		Object[] obarray = hset.toArray();
		Object[][] barray=new Block[hset.size()][2];
		int i=0;
		for(Object ob : obarray){
			barray[i][0]=(Block)ob;
			barray[i][1]=hash.get((Block)ob);
			i++;
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
					sender.sendMessage("/"+cmd.getName()+" {delete/remove} // delete all selected blocks");
					sender.sendMessage("/"+cmd.getName()+" {type} (MaterialName) // change the block type of all selected to the input material");
					sender.sendMessage("/"+cmd.getName()+" {clear} // clear your clipboard");
				}
				registerLastCommand(cmd.getName(), args);
				return true;
			}
			if(args[0].equalsIgnoreCase("wall")){
				if(args.length==1){
					sender.sendMessage("/"+cmd.getName()+" wall {height}");
					sender.sendMessage("Takes the selected and extends it");
					sender.sendMessage("up the number on layers you input.");
					sender.sendMessage("This is for making walls and other");
					sender.sendMessage("high structures");
					registerLastCommand(cmd.getName(), args);
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
				registerLastCommand(cmd.getName(), args);
				return true;
			}
			if(args[0].equalsIgnoreCase("cube")||args[0].equalsIgnoreCase("box")||args[0].equalsIgnoreCase("cuboid")){
				int blocks = 0;
				for(double x = Math.min(this.c1.get((Player)sender).getX(), this.c2.get((Player)sender).getX());x<Math.max(this.c1.get((Player)sender).getX(), this.c2.get((Player)sender).getX())+1;x++){
					for(double y = Math.min(this.c1.get((Player)sender).getY(), this.c2.get((Player)sender).getY());y<Math.max(this.c1.get((Player)sender).getY(), this.c2.get((Player)sender).getY())+1;y++){
						for(double z = Math.min(this.c1.get((Player)sender).getZ(), this.c2.get((Player)sender).getZ());z<Math.max(this.c1.get((Player)sender).getZ(), this.c2.get((Player)sender).getZ())+1;z++){
							Block block = ((Player)sender).getWorld().getBlockAt(new Location(((Player)sender).getWorld(), x, y, z));
							if(block.getType()!=Material.AIR){
								addClip(block, (Player)sender, false);
								blocks++;
							}
						}
					}
				}
				sender.sendMessage(ChatColor.GOLD+"You just selected "+blocks+" blocks!");
			}
			if(args[0].equalsIgnoreCase("uncube")||args[0].equalsIgnoreCase("unbox")||args[0].equalsIgnoreCase("uncuboid")||args[0].equalsIgnoreCase("un_cube")||args[0].equalsIgnoreCase("un_box")||args[0].equalsIgnoreCase("un_cuboid")){
				if(this.c1.containsKey((Player)sender)){
					this.c1.remove((Player)sender);
				}
				if(this.c2.containsKey((Player)sender)){
					this.c2.remove((Player)sender);
					sender.sendMessage("you have cleared your Corner Selection");
				}
			}
			if(args[0].equalsIgnoreCase("shift")){
				if(args.length<4){
					sender.sendMessage("/"+cmd.getName()+" {shift} {x_amount} {y_amount} {z_amount}");
					sender.sendMessage("This will move everything selected over the selected units");
					sender.sendMessage("and will fill the original places with air");
					registerLastCommand(cmd.getName(), args);
					return true;
				}
				int xa = Integer.parseInt(args[1]);
				int ya = Integer.parseInt(args[2]);
				int za = Integer.parseInt(args[3]);
				Block[] bl = hashToArray(clip);
				World world = ((Player)sender).getWorld();
				HashMap<Block, Material> blocks = new HashMap<Block, Material>();
				for(Block b : bl){
					if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
						Location loc = b.getLocation();
						loc.setX(loc.getX()+xa);
						loc.setY(loc.getY()+ya);
						loc.setZ(loc.getZ()+za);
						Block block=world.getBlockAt(loc);
						blocks.put(block, b.getType());
						b.setType(Material.AIR);
					}
				}
				
				Set<Block> bll = blocks.keySet();
				
				for(Block block : bll){
					
					Block b = block;
					
					b.setType(blocks.get(b));
					
				}
				
				registerLastCommand(cmd.getName(), args);
				return true;
			}
			if(args[0].equalsIgnoreCase("pan")){
				if(args.length<3){
					sender.sendMessage("/"+cmd.getName()+" {pan} {x_amount} {z_amount}");
					sender.sendMessage("Same as shift only with locked height");
					registerLastCommand(cmd.getName(), args);
					return true;
				}
				int xa = Integer.parseInt(args[1]);
				int za = Integer.parseInt(args[2]);
				Block[] bl = hashToArray(clip);
				World world = ((Player)sender).getWorld();
				HashMap<Block, Material> blocks = new HashMap<Block, Material>();
				for(Block b : bl){
					if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
						Location loc = b.getLocation();
						loc.setX(loc.getX()+xa);
						loc.setZ(loc.getZ()+za);
						Block block=world.getBlockAt(loc);
						blocks.put(block, b.getType());
						b.setType(Material.AIR);
					}
				}
				Set<Block> bll = blocks.keySet();
				
				for(Block block : bll){
					
					Block b = block;
					
					b.setType(blocks.get(b));
					
				}
				
				registerLastCommand(cmd.getName(), args);
				return true;
			}
			if(args[0].equalsIgnoreCase("clear")){
				Block[] bl = hashToArray(clip);
				for(Block b : bl){
					if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
						clip.remove(b);
					}
				}
				registerLastCommand(cmd.getName(), args);
				return true;
			}
			if(args[0].equalsIgnoreCase("l")||args[0].equalsIgnoreCase("last")){
				if(args.length==1){
					getServer().dispatchCommand(sender, this.lcommand);
					return true;
				}
				int amount = Integer.parseInt(args[0]);
				for(int i=0;i<(amount+1);i++){
					getServer().dispatchCommand(sender, this.lcommand);
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("remove")){
				Block[] bl = hashToArray(clip);
				for(Block b : bl){
					if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
						b.setType(Material.AIR);
					}
				}
			}
			if(args[0].equalsIgnoreCase("type")){
				Block[] bl = hashToArray(clip);
				if(args.length==1){
					sender.sendMessage("Invalid amount of arguments");
					return true;
				}
				if(args.length==2){
					for(Block b : bl){
						if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
							Material mat = Material.valueOf(args[1]);
							if(mat==null){
								sender.sendMessage("No Material named "+args[1]);
								return true;
							}
							b.setType(mat);
						}
					}
					return true;
				}
				if(args.length>2){
					for(Block b : bl){
						if(clip.get(b).getDisplayName().equalsIgnoreCase(((Player)sender).getDisplayName())){
							Material mat = Material.valueOf(args[1]);
							Material mat2 = Material.valueOf(args[2]);
							if(mat==null){
								sender.sendMessage("No Material named "+args[1]);
								return true;
							}
							if(b.getType()==mat2){
								b.setType(mat);
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}
	public Object[] getSelectionTool(){
		Object[] mat=new Object[2];
		mat[0] = Material.valueOf(this.tool_name.toUpperCase());
		if(mat[0]==null){
			mat[0] = Material.STICK;
		}
		mat[1]=this.tool_id;
		return mat;
	}
	
	public Object[] getCubeSelectionTool(){
		Object[] mat=new Object[2];
		mat[0] = Material.valueOf(this.cube_tool_name.toUpperCase());
		if(mat[0]==null){
			mat[0] = Material.STICK;
		}
		mat[1]=this.cube_tool_id;
		return mat;
	}
	
	/**
	 * Adds or removes a block with the value of the player who clicked it to the clipboard
	 * 
	 * @param block : the block clicked
	 * @param player : the player who clicked
	 * @return a boolean - false if block was removed and true if it was added
	 */
	public boolean addClip(Block block, Player player, boolean msg){
		if(clip.containsKey(block)){
			if(clip.get(block).getDisplayName().equalsIgnoreCase(player.getDisplayName())){
				clip.remove(block);
				if(msg){
					player.sendMessage("Block Removed From Clipboard");
				}
				return false;
			}else{
				if(msg){
					player.sendMessage("Someone Else Has Selected This Block");
				}
				return false;
			}
		}else{
			clip.put(block, player);
			if(msg){
				player.sendMessage("Block Added To Clipboard");
			}
			return true;
		}
	}
	public String assemble(String[] sl, String sep){
		String ret="";
		for(String s : sl){
			ret+=s;
			ret+=sep;
		}
		return ret;
	}
	public void registerLastCommand(String label, String[] args){
		this.lcommand=label+" "+assemble(args, " ");
	}
	public void setC1(Player p, Location l){
		this.c1.put(p, l);
	}
	public void setC2(Player p, Location l){
		this.c2.put(p, l);
	}
	public Location getC1(Player p){
		if(!this.c1.containsKey(p)){
			return p.getLocation();
		}
		return this.c1.get(p);
	}
	public Location getC2(Player p){
		if(!this.c2.containsKey(p)){
			return p.getLocation();
		}
		return this.c2.get(p);
	}
	public Object[] getCuboidSelectTool(){
		Object[] mat=new Object[2];
		mat[0] = Material.valueOf(this.cube_tool_name.toUpperCase());
		if(mat[0]==null){
			mat[0] = Material.STRING;
		}
		mat[1]=this.cube_tool_id;
		return mat;
	}
}
