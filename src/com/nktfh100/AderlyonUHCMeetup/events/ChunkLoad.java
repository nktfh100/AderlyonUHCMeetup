package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoad implements Listener {

	@EventHandler
	public void chunkLoad(ChunkLoadEvent ev) {
		if (ev.getWorld().getName().equals("uhc")) {
			for (Entity entity : ev.getChunk().getEntities()) {
				if (!(entity instanceof Player) && entity instanceof LivingEntity) {
					entity.remove();
				}
			}
		}
	}
}
