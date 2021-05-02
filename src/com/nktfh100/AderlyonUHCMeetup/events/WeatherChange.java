package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChange implements Listener {

	@EventHandler
	public void weatherChange(WeatherChangeEvent ev) {
		if (ev.getWorld().getName().equals("uhc")) {
			ev.setCancelled(true);
		}
	}
}
