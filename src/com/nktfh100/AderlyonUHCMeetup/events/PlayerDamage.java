package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class PlayerDamage implements Listener {
	@EventHandler
	public void entityDamage(EntityDamageEvent ev) {
		if (ev.getEntity() instanceof Player) {
			if (UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING || UHCMeetup.getInstance().getGameManager().getGameFinished()) {
				ev.setCancelled(true);
				return;
			}
			if (UHCMeetup.getInstance().getPlayersManager().getPlayerInfo((Player) ev.getEntity()).isSpectating()) {
				ev.setCancelled(true);
			}
		}
	}
}
