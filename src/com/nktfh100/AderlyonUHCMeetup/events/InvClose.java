package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.inventory.CustomHolder;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class InvClose implements Listener {
	@EventHandler
	public void onInvClose(InventoryCloseEvent ev) {
		Player player = (Player) ev.getPlayer();
		PlayerInfo pInfo = UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(player);
		if (pInfo != null) {
			InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
			if (holder instanceof CustomHolder) {
				((CustomHolder) holder).invClosed();
			}
		}
	}
}
