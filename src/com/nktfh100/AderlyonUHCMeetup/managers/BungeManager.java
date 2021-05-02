package com.nktfh100.AderlyonUHCMeetup.managers;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.info.BungeArena;
import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.inventory.BungeArenaSelectorInv;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;

public class BungeManager implements PluginMessageListener {

	private UHCMeetup plugin;

	private HashMap<String, BungeArena> gameServers = new HashMap<String, BungeArena>();

	private BukkitTask statusRunnable = null;
	private BungeArenaSelectorInv arenaSelectorInv;

	public BungeManager(UHCMeetup instance, ArrayList<String> gameServers_) {
		this.plugin = instance;
		if (gameServers_ == null) {
			return;
		}
		if (this.plugin.getIsLobby()) {
			for (String server : gameServers_) {
				String[] serverSplit = server.split(",");
				if (serverSplit.length >= 3) {
					this.gameServers.put(server, new BungeArena(serverSplit[0], serverSplit[1], Integer.parseInt(serverSplit[2]), GameState.LOADING, 0, 0));
				}
			}

			this.arenaSelectorInv = new BungeArenaSelectorInv();

			BungeManager bungeManager = this;
			this.statusRunnable = new BukkitRunnable() {

				@Override
				public void run() {
					bungeManager.getGameServersInfo();
				}
			}.runTaskTimerAsynchronously(this.plugin, 20L * 3L, 20L * 10L);
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord") || !this.plugin.getIsLobby()) {
			return;
		}
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String subchannel = in.readUTF();
			if (subchannel.equals("UHCMeetup")) {
				short len = in.readShort();
				byte[] data = new byte[len];
				in.readFully(data);

				String s = new String(data);

				String[] dataStr = s.split(",");
				if (dataStr.length >= 4 && this.gameServers.get(dataStr[0]) != null) {
					BungeArena ba = this.gameServers.get(dataStr[0]);
					ba.setGameState(GameState.valueOf(dataStr[1]));
					ba.setCurrentPlayers(Integer.valueOf(dataStr[2]));
					ba.setMaxPlayers(Integer.valueOf(dataStr[3]));
					this.updateArenaSelectorInv();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Should be run async
	public void getGameServersInfo() {
		if (!this.plugin.isEnabled()) {
			return;
		}

		for (BungeArena arena : this.getAllArenas()) {
			try {
				arena.getServerStatus().update();
				String motd = arena.getServerStatus().getStatus();
				if (!motd.isEmpty()) {
					String[] motdSplit = motd.split(",");
					if (motdSplit.length >= 4) {
//						String serverName = motdSplit[0];
						arena.setGameState(GameState.valueOf(motdSplit[1]));
						arena.setCurrentPlayers(Integer.parseInt(motdSplit[2]));
						arena.setMaxPlayers(Integer.parseInt(motdSplit[3]));
					}
				}
			} catch (IOException e) {
//				e.printStackTrace();
				arena.setGameState(GameState.LOADING);
				arena.setCurrentPlayers(0);
			}
		}
		this.updateArenaSelectorInv();
	}

	public BungeArena getArenaWithMostPlayers() {
		if (this.gameServers.size() == 0) {
			return null;
		}
		if (this.gameServers.size() == 1) {
			return this.gameServers.values().iterator().next();
		}
		ArrayList<BungeArena> arenas_ = new ArrayList<BungeArena>(this.getAllArenas());
		BungeArena arena = null;

		for (BungeArena arena_ : arenas_) {
			if (arena_.getGameState() == GameState.RUNNING || arena_.getGameState() == GameState.LOADING) {
				continue;
			}
			if (arena_.getCurrentPlayers() == arena_.getMaxPlayers()) {
				continue;
			}
			if (arena == null) {
				arena = arena_;
				continue;
			}
			if (arena_.getCurrentPlayers() > arena.getCurrentPlayers()) {
				arena = arena_;
			}
		}

		return arena;
	}

	public BungeArena getRandomArena() {
		if (this.gameServers.size() == 0) {
			return null;
		}
		if (this.gameServers.size() == 1) {
			return this.gameServers.values().iterator().next();
		}
		ArrayList<BungeArena> arenas_ = new ArrayList<BungeArena>();
		for (BungeArena ba : this.getAllArenas()) {
			if (ba.getGameState() == GameState.RUNNING || ba.getGameState() == GameState.LOADING) {
				continue;
			}
			if (ba.getCurrentPlayers() == ba.getMaxPlayers()) {
				continue;
			}
			arenas_.add(ba);
		}
		if (arenas_.size() == 0) {
			return null;
		}
		if (arenas_.size() == 1) {
			return arenas_.get(0);
		}
		return arenas_.get(Utils.getRandomNumberInRange(0, arenas_.size() - 1));
	}

	public void openArenaSelector(PlayerInfo pInfo) {
		if (this.arenaSelectorInv != null) {
			pInfo.getPlayer().openInventory(this.arenaSelectorInv.getInventory());
		}
	}

	public void updateArenaSelectorInv() {
		if (this.arenaSelectorInv != null) {
			this.arenaSelectorInv.update();
		}
	}

	public BungeArena getArenaByServer(String server) {
		if (this.gameServers.size() > 0) {
			return (this.gameServers.get(server));
		} else {
			return null;
		}
	}

	public Collection<BungeArena> getAllArenas() {
		return this.gameServers.values();
	}

	public HashMap<String, BungeArena> getArenas_() {
		return this.gameServers;
	}

	public ArrayList<String> getAllArenasServerNames() {
		return new ArrayList<String>(this.gameServers.keySet());
	}

}
