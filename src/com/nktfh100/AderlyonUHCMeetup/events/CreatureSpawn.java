package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
public class CreatureSpawn implements Listener {

	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent ev) {
		if (ev.getLocation().getWorld().getName().equals("uhc")) {
			ev.setCancelled(true);
		}
	}
}
