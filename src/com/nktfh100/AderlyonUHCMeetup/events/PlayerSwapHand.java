package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class PlayerSwapHand implements Listener {

	@EventHandler
	public void swapHand(PlayerSwapHandItemsEvent ev) {
		if (UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
		}
	}

}
