package org.styllex.clipboard.listener;

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
			if(player.getItemInHand().getType()==plugin.getSelectionTool()){
				plugin.addClip(block, player);
			}
		}
	}
}
