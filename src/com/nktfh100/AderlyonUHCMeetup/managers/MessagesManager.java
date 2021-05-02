package com.nktfh100.AderlyonUHCMeetup.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;

import me.clip.placeholderapi.PlaceholderAPI;

public class MessagesManager {

	private UHCMeetup plugin;

	private HashMap<String, String> msgsGame = new HashMap<String, String>();
	private HashMap<String, String> deathMessages = new HashMap<String, String>();
	private HashMap<String, String> scoreboard = new HashMap<String, String>();
	private HashMap<String, ArrayList<String>> scoreboardLines = new HashMap<String, ArrayList<String>>();
	private HashMap<String, String> scenariosNames = new HashMap<String, String>();

	public MessagesManager(UHCMeetup instance) {
		plugin = instance;
	}

	public void loadAll() {
		File msgsConfigFIle = new File(this.plugin.getDataFolder(), "messages.yml");
		if (!msgsConfigFIle.exists()) {
			try {
				this.plugin.saveResource("messages.yml", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		YamlConfiguration msgsConfig = YamlConfiguration.loadConfiguration(msgsConfigFIle);
		try {
			this.msgsGame = new HashMap<String, String>();
			this.scoreboard = new HashMap<String, String>();
			this.scoreboardLines = new HashMap<String, ArrayList<String>>();

			final String prefix = this.plugin.getConfigManager().getPrefix();

			ConfigurationSection gameMsgsSC = msgsConfig.getConfigurationSection("messages");
			Set<String> gameMsgsKeys = gameMsgsSC.getKeys(false);
			for (String key : gameMsgsKeys) {
				this.msgsGame.put(key, ChatColor.translateAlternateColorCodes('&', gameMsgsSC.getString(key).replaceAll("%prefix%", prefix)));
			}

			ConfigurationSection deathMsgsSC = msgsConfig.getConfigurationSection("deathMessages");
			Set<String> deathMsgsKeys = deathMsgsSC.getKeys(false);
			for (String key : deathMsgsKeys) {
				this.deathMessages.put(key, ChatColor.translateAlternateColorCodes('&', deathMsgsSC.getString(key).replaceAll("%prefix%", prefix)));
			}

			ConfigurationSection scenariosNamesSC = msgsConfig.getConfigurationSection("scenarios");
			Set<String> scenariosNamesKeys = scenariosNamesSC.getKeys(false);
			for (String key : scenariosNamesKeys) {
				this.scenariosNames.put(key, ChatColor.translateAlternateColorCodes('&', scenariosNamesSC.getString(key)));
			}

			ConfigurationSection scoreboardSC = msgsConfig.getConfigurationSection("scoreboard");
			Set<String> scoreboardKeys = scoreboardSC.getKeys(false);
			for (String key : scoreboardKeys) {
				this.scoreboard.put(key, ChatColor.translateAlternateColorCodes('&', scoreboardSC.getString(key).replaceAll("%prefix%", prefix)));
			}

			this.scoreboardLines.put("waiting-lobby", new ArrayList<String>());
			for (String line : scoreboardSC.getStringList("waiting-lobby-lines")) {
				this.scoreboardLines.get("waiting-lobby").add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", prefix)));
			}

			this.scoreboardLines.put("playing", new ArrayList<String>());
			for (String line : scoreboardSC.getStringList("playing-lines")) {
				this.scoreboardLines.get("playing").add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", prefix)));
			}

			this.scoreboardLines.put("spectator", new ArrayList<String>());
			for (String line : scoreboardSC.getStringList("spectator-lines")) {
				this.scoreboardLines.get("spectator").add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", prefix)));
			}

		} catch (Exception e) {
			e.printStackTrace();
			plugin.getLogger().log(Level.SEVERE, "Something is wrong with your messages.yml file!");
			this.plugin.getPluginLoader().disablePlugin(this.plugin);
		}
	}

	private String replaceExtra(String line, String... extras) {
		if (line == null) {
			return "";
		}
		int i = 0;
		for (String extra : extras) {
			if (i == 0) {
				line = line.replaceAll("%value%", extra);
			} else {
				line = line.replaceAll("%value" + i + "%", extra);
			}
			i++;
		}
		return line;
	}

	public String getGameMsg(String key, String... extras) {
		if (this.msgsGame == null) {
			return "";
		}
		String output = this.msgsGame.get(key);
		if (output == null) {
			this.plugin.getLogger().warning("Game message '" + key + "' is missing from your messages.yml file!");
			return "";
		}
		return replaceExtra(output, extras);
	}

	public String getScoreboard(String key) {
		String output = this.scoreboard.get(key);
		if (output == null) {
			return "";
		}
		return output;
	}

	public String getDeathMsg(String key, Player player, Player killer, Integer blocks) {
		String out = this.deathMessages.get(key);
		out = out.replaceAll("%victim%", player.getName());
		PlayerInfo pInfo = this.plugin.getPlayersManager().getPlayerInfo(player);
		out = out.replaceAll("%killcount%", pInfo.getKills() + "");

		if (killer != null) {
			out = out.replaceAll("%killer%", killer.getName());
			PlayerInfo killerInfo = this.plugin.getPlayersManager().getPlayerInfo(killer);
			out = out.replaceAll("%killerkillcount%", killerInfo.getKills() + "");
		}
		out = out.replaceAll("%blocks%", blocks + "");

		return out;
	}

	public String getScoreboardLine(String team, int i, PlayerInfo pInfo) {
		String line = this.scoreboardLines.get(team).get(i);
		line = line.replaceAll("%emptyline%", Utils.getRandomColors());
		if (pInfo != null) {

			line = line.replaceAll("%player%", pInfo.getPlayer().getName());
			line = line.replaceAll("%players%", this.plugin.getGameManager().getPlayers().size() + "");
			line = line.replaceAll("%minplayers%", this.plugin.getConfigManager().getMinPlayers() + "");
			line = line.replaceAll("%maxplayers%", this.plugin.getConfigManager().getMaxPlayers() + "");
			line = line.replaceAll("%gamestate%", this.plugin.getGameManager().getGameState().toString());

			line = line.replaceAll("%gamestarttime%",
					this.plugin.getGameManager().getGameState() == GameState.WAITING ? this.plugin.getConfigManager().getGameTimer() + "" : this.plugin.getGameManager().getGameTimer() + "");

			line = line.replaceAll("%playersalive%", this.plugin.getGameManager().getPlayersAlive().size() + "");
			line = line.replaceAll("%gametime%", this.plugin.getGameManager().getGameTime() + "");
			line = line.replaceAll("%kills%", pInfo.getKills() + "");
			if (this.plugin.getGameManager().getActiveBorderStage() != null) {
				line = line.replaceAll("%border%", this.plugin.getGameManager().getActiveBorderStage().getSize() + "");
				line = line.replaceAll("%bordertime%", this.plugin.getGameManager().getTimeToNextBorder() + "");
			}

		}
		if (this.plugin.getIsPlaceHolderAPI()) {
			line = PlaceholderAPI.setPlaceholders(pInfo.getPlayer(), line);
		}
		return line;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getScoreBoardLines(String key) {
		ArrayList<String> out = this.scoreboardLines.get(key);
		return (ArrayList<String>) out.clone();
	}

	public String getScenarioName(String stName) {
		return this.scenariosNames.get(stName);
	}
}
