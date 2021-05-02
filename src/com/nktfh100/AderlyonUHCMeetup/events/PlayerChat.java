package com.nktfh100.AderlyonUHCMeetup.events;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

@SuppressWarnings("deprecation")
public class PlayerChat implements Listener {

	@EventHandler
	public void playerChat(PlayerChatEvent ev) {

		if (UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(ev.getPlayer()).isSpectating()) {
			ArrayList<Player> playersAlive = new ArrayList<Player>();
			for (PlayerInfo pInfo_ : UHCMeetup.getInstance().getGameManager().getPlayersAlive()) {
				playersAlive.add(pInfo_.getPlayer());
			}
			ev.getRecipients().removeAll(playersAlive);
		}
	}
}
