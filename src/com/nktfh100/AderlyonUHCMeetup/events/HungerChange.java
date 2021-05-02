package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class HungerChange implements Listener {

	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent ev) {
		if (UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
			return;
		}
		if (UHCMeetup.getInstance().getPlayersManager().getPlayerInfo((Player) ev.getEntity()).isSpectating()) {
			ev.setCancelled(true);
		}
	}
}
