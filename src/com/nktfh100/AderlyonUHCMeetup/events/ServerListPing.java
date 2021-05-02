package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class ServerListPing implements Listener {

	@EventHandler
	public void serverListPingEvent(ServerListPingEvent ev) {
		ev.setMotd(this.getMotd());
	}

	private String getMotd() {
		String out = UHCMeetup.getInstance().getConfigManager().getServerName() + "," + UHCMeetup.getInstance().getGameManager().getGameState().toString() + ","
				+ UHCMeetup.getInstance().getGameManager().getPlayers().size() + "," + UHCMeetup.getInstance().getConfigManager().getMaxPlayers();
		return out;
	}
}
