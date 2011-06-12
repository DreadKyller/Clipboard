package org.styllex.clipboard.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.styllex.clipboard.Mega;

public class IC3D_Listener_10_90_13 extends PlayerListener{
	
	private Mega plugin;
	
	public IC3D_Listener_10_90_13(Mega instance){
		this.plugin=instance;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();
			if(player.getItemInHand().getType()==((Material)plugin.getSelectionTool()[0]))
			{
				plugin.addClip(block, player, true);
			}
			if(player.getItemInHand().getTypeId()==((int)((Integer)plugin.getSelectionTool()[1])))
			{
				plugin.addClip(block, player, true);
			}
			if(player.getItemInHand().getType()==((Material)plugin.getCuboidSelectTool()[0]))
			{
				if(plugin.c3.containsKey(player)){
					if(plugin.c3.get(player)==1){
						plugin.setC1(player, event.getClickedBlock().getLocation());
						player.sendMessage(ChatColor.GOLD+"You Selected Corner 1 Of The Cuboid!");
						plugin.c3.put(player, 2);
					}else{
						plugin.setC2(player, event.getClickedBlock().getLocation());
						player.sendMessage(ChatColor.GOLD+"You Selected Corner 2 Of The Cuboid!");
						plugin.c3.put(player, 1);
					}
				}else{
					plugin.setC1(player, event.getClickedBlock().getLocation());
					player.sendMessage(ChatColor.GOLD+"You Selected Corner 1 Of The Cuboid!");
					plugin.c3.put(player, 2);
				}
			}
			if(player.getItemInHand().getTypeId()==((Integer)plugin.getCuboidSelectTool()[1]))
			{
				if(plugin.c3.containsKey(player)){
					if(plugin.c3.get(player)==1){
						plugin.setC1(player, event.getClickedBlock().getLocation());
						player.sendMessage(ChatColor.GOLD+"You Selected Corner 1 Of The Cuboid!");
						plugin.c3.put(player, 2);
					}else{
						plugin.setC2(player, event.getClickedBlock().getLocation());
						player.sendMessage(ChatColor.GOLD+"You Selected Corner 2 Of The Cuboid!");
						plugin.c3.put(player, 1);
					}
				}else{
					plugin.setC1(player, event.getClickedBlock().getLocation());
					player.sendMessage(ChatColor.GOLD+"You Selected Corner 1 Of The Cuboid!");
					plugin.c3.put(player, 2);
				}
			}
		}
	}
}
