package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.inventory.CustomHolder;
import com.nktfh100.AderlyonUHCMeetup.inventory.Icon;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class InvClick implements Listener {

	@EventHandler
	public void invClick(InventoryClickEvent ev) {
		Player player = (Player) ev.getWhoClicked();

		if (!UHCMeetup.getInstance().getIsLobby() && UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
		}
		if (!UHCMeetup.getInstance().getIsLobby() && UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(player).isSpectating()) {
			ev.setCancelled(true);
		}
		Inventory inv = ev.getClickedInventory();
		if (inv != null && inv.getHolder() != null && inv.getHolder() instanceof CustomHolder) {
			ev.setCancelled(true);
			CustomHolder customHolder = (CustomHolder) ev.getView().getTopInventory().getHolder();
			Icon icon = customHolder.getIcon(ev.getRawSlot());
			if (icon != null) {
				icon.executeActions(player);
			}
			return;
		}
	}
}
