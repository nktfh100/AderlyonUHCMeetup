package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class PlayerBlockBreak implements Listener {
	@EventHandler
	public void onPlayerBreak(BlockBreakEvent ev) {
		if (UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
		}
		if (UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(ev.getPlayer()).isSpectating()) {
			ev.setCancelled(true);
		}
	}
}
