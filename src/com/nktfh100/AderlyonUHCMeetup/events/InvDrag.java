package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class InvDrag implements Listener {

	@EventHandler
	public void inventoryDrag(InventoryDragEvent ev) {
		if (UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
		}
	}
}
