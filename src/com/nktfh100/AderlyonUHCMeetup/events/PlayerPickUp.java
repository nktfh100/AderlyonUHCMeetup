package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

@SuppressWarnings("deprecation")
public class PlayerPickUp implements Listener {

	@EventHandler
	public void entityPickupItem(PlayerPickupItemEvent ev) {
		if (UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
		}
		if (UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(ev.getPlayer()).isSpectating()) {
			ev.setCancelled(true);
		}
	}
}
