package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class PlayerRegainHealth implements Listener {

	@EventHandler
	public void entityRegainHealth(EntityRegainHealthEvent ev) {
		if (ev.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || ev.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) {
			ev.setCancelled(true);
		}
	}
}
