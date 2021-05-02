package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class PlayerDrop implements Listener {

	@EventHandler
	public void playerItemDrop(PlayerDropItemEvent ev) {
		if (UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
		}
		if (UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(ev.getPlayer()).isSpectating()) {
			ev.setCancelled(true);
		}
	}
}
