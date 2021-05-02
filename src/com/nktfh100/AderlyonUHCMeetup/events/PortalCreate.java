package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

public class PortalCreate implements Listener {

	@EventHandler
	public void portalCreate(PortalCreateEvent ev) {
		if (ev.getWorld().getName().equals("uhc")) {
			ev.setCancelled(true);
		}
	}
}
