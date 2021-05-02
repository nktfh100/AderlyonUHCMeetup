package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class PlayerRespawn implements Listener {
	@EventHandler
	public void onRespawn(PlayerRespawnEvent ev) {
		if (UHCMeetup.getInstance().getGameManager().getGameState() == GameState.RUNNING) {
			ev.setRespawnLocation(new Location(UHCMeetup.getInstance().getWorldManager().getWorld(), 0, UHCMeetup.getInstance().getWorldManager().getWorld().getHighestBlockYAt(0, 0) + 10, 0));
		}
	}
}
