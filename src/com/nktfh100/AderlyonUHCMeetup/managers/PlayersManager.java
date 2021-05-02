package com.nktfh100.AderlyonUHCMeetup.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class PlayersManager implements Listener {

	private UHCMeetup plugin;

	private HashMap<String, PlayerInfo> players = new HashMap<>();

	public PlayersManager(UHCMeetup instance) {
		this.plugin = instance;
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			players.put(player.getUniqueId().toString(), new PlayerInfo(player));
		}
		for (PlayerInfo pInfo : this.getPlayers()) {
			pInfo.getStatsManager().mysql_registerPlayer(true);
		}
	}

	public PlayerInfo _addPlayer(Player player) {
		PlayerInfo out = new PlayerInfo(player);
		out.getStatsManager().mysql_registerPlayer(true);
		players.put(player.getUniqueId().toString(), out);
		return out;
	}

	public PlayerInfo getPlayerInfo(Player player) {
		if (player == null) {
			return null;
		}
		PlayerInfo pInfo = players.get(player.getUniqueId().toString());
		if (pInfo == null) {
			pInfo = this._addPlayer(player);
		}
		return pInfo;
	}

	public PlayerInfo getPlayerByUUID(String uuid) {
		return this.players.get(uuid);
	}

	public List<PlayerInfo> getPlayers() {
		List<PlayerInfo> players_ = new ArrayList<PlayerInfo>(this.players.values());
		return players_;
	}

	public ArrayList<StatsManager> getAllStatsManagers() {
		ArrayList<StatsManager> out = new ArrayList<StatsManager>();
		for (PlayerInfo pInfo : this.getPlayers()) {
			out.add(pInfo.getStatsManager());
		}
		return out;
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent ev) {
		if (this.plugin.getIsLobby()) {
			return;
		}
		if (this.plugin.getGameManager().getGameState() == GameState.RUNNING) {
			if (ev.getPlayer().hasPermission("aderlyon-uhc-meetup.admin") || ev.getPlayer().hasPermission("aderlyon-uhc-meetup.admin")) {
				ev.allow();
			} else {
				ev.disallow(PlayerLoginEvent.Result.KICK_OTHER, this.plugin.getMessagesManager().getGameMsg("gameRunningKick"));
			}
		} else if (this.plugin.getGameManager().getGameState() == GameState.LOADING) {
			if (ev.getPlayer().hasPermission("aderlyon-uhc-meetup.admin") || ev.getPlayer().hasPermission("aderlyon-uhc-meetup.admin")) {
				ev.allow();
			} else {
				ev.disallow(PlayerLoginEvent.Result.KICK_OTHER, this.plugin.getMessagesManager().getGameMsg("gameLoadingKick"));
			}
		} else if (this.plugin.getGameManager().getPlayers().size() >= this.plugin.getConfigManager().getMaxPlayers()) {
			if (ev.getPlayer().hasPermission("aderlyon-uhc-meetup.admin") || ev.getPlayer().hasPermission("aderlyon-uhc-meetup.admin")) {
				ev.allow();
			} else {
				ev.disallow(PlayerLoginEvent.Result.KICK_OTHER, this.plugin.getMessagesManager().getGameMsg("gameFullKick"));
			}
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		Player player = ev.getPlayer();
		if (players.get(ev.getPlayer().getUniqueId().toString()) == null) {
			players.put(ev.getPlayer().getUniqueId().toString(), new PlayerInfo(ev.getPlayer()));
		} else {
			players.get(ev.getPlayer().getUniqueId().toString())._setPlayer(player);
		}
		this.getPlayerInfo(player).getStatsManager().mysql_registerPlayer(true);

		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(40);

		if (this.plugin.getIsLobby()) {
			player.getInventory().clear();
			player.getInventory().setItem(this.plugin.getConfigManager().getLobbyItemSlot("lobbyStats"), this.plugin.getItemsManager().getItem("stats").getItem().getItem());
			player.getInventory().setItem(this.plugin.getConfigManager().getLobbyItemSlot("arenaSelector"), this.plugin.getItemsManager().getItem("arenaSelector").getItem().getItem());
			if (Bukkit.getOnlinePlayers().size() <= 2) {
				new BukkitRunnable() {
					@Override
					public void run() {
						plugin.getBungeManager().getGameServersInfo();
					}
				}.runTaskLaterAsynchronously(this.plugin, 5L);
			}
			return;
		}
		player.teleport(this.plugin.getConfigManager().getWaitingLobby());
		ev.setJoinMessage(null);
		if (this.plugin.getGameManager().getGameState() == GameState.WAITING || this.plugin.getGameManager().getGameState() == GameState.STARTING) {
			if (this.plugin.getGameManager().getPlayers().size() < this.plugin.getConfigManager().getMaxPlayers()) {
				this.plugin.getGameManager().playerJoin(player);
				ev.setJoinMessage(this.plugin.getMessagesManager().getGameMsg("playerJoin", player.getName(), this.plugin.getGameManager().getPlayers().size() + "",
						this.plugin.getConfigManager().getMaxPlayers() + "", this.plugin.getConfigManager().getMinPlayers() + ""));
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent ev) {
		Player player = ev.getPlayer();
		PlayerInfo pInfo = this.players.get(player.getUniqueId().toString());
		if (pInfo == null) {
			return;
		}
		if (this.plugin.getIsLobby()) {
			players.remove(player.getUniqueId().toString());
			return;
		}
		ev.setQuitMessage(null);
		this.plugin.getGameManager().playerLeave(pInfo);
		if (this.plugin.getGameManager().getGameState() == GameState.WAITING || this.plugin.getGameManager().getGameState() == GameState.STARTING) {
			ev.setQuitMessage(this.plugin.getMessagesManager().getGameMsg("playerLeave", player.getName(), this.plugin.getGameManager().getPlayers().size() + "",
					this.plugin.getConfigManager().getMaxPlayers() + "", this.plugin.getConfigManager().getMinPlayers() + ""));
			this.plugin.getGameManager().getScenarioVoteInv().playerLeft(player);
		}
		if (this.plugin.getGameManager().getGameState() == GameState.RUNNING) {
			pInfo.getStatsManager().saveStats(true);
		}
		if (this.plugin.getGameManager().getGameState() == GameState.WAITING || this.plugin.getGameManager().getGameState() == GameState.STARTING) {
			players.remove(player.getUniqueId().toString());
		}
	}
}
