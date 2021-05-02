package com.nktfh100.AderlyonUHCMeetup.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.nktfh100.AderlyonUHCMeetup.info.BorderStage;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class ConfigManager {

	private UHCMeetup plugin;

	private String prefix;

	private String bungeecordLobbyServer;
	private String serverName;
	private ArrayList<String> gameServers;

	private Location waitingLobby;
	private Integer minPlayers;
	private Integer maxPlayers;
	private Integer gameTimer;
	private Integer borderHeight;
	private Boolean dropGoldenHead;
	private String defaultKit;
	private Boolean showRunningArenas;
	private Integer defaultElo;

	private Boolean mysqlEnabled = true;
	private String mysql_host = "";
	private String mysql_port = "";
	private String mysql_database = "";
	private String mysql_username = "";
	private String mysql_password = "";
	private Connection mysql_connection = null;

	private HashMap<String, Integer> lobbyItemsSlots = null;
	private ArrayList<PotionEffect> goldenHeadEffects = new ArrayList<PotionEffect>();
	private ArrayList<BorderStage> borderStages = new ArrayList<BorderStage>();
	private ArrayList<PotionEffect> noCleanEffects = new ArrayList<PotionEffect>();

	public ConfigManager(UHCMeetup instance) {
		this.plugin = instance;
	}

	public void loadConfig() {
		this.plugin.saveDefaultConfig();
		this.loadConfigVars();
	}

	public void loadConfigVars() {
		this.lobbyItemsSlots = new HashMap<String, Integer>();
		this.goldenHeadEffects = new ArrayList<PotionEffect>();
		this.borderStages = new ArrayList<BorderStage>();

		this.plugin.reloadConfig();
		FileConfiguration config = this.plugin.getConfig();

		if (config.getConfigurationSection("bungeecord") != null) {
			ConfigurationSection bungeSec = config.getConfigurationSection("bungeecord");
			this.plugin.setIsLobby(bungeSec.getBoolean("isLobby", false));
			this.bungeecordLobbyServer = bungeSec.getString("lobbyServer", "lobby");
			this.serverName = bungeSec.getString("serverName", "null");
			this.gameServers = (ArrayList<String>) bungeSec.getStringList("gameServers");
		}

		String waitingLobbyStr = config.getString("waitingLobby", "world,0,0,0,0,0");
		if (waitingLobbyStr.equalsIgnoreCase("world,0,0,0,0,0")) {
			this.waitingLobby = Bukkit.getWorlds().get(0).getSpawnLocation();
		} else {
			String[] waitingLobbyStrSplit = waitingLobbyStr.split(",");
			World world = Bukkit.getWorld(waitingLobbyStrSplit[0]);
			if (world == null) {
				this.waitingLobby = Bukkit.getWorlds().get(0).getSpawnLocation();
			} else {
				Double x = Double.parseDouble(waitingLobbyStrSplit[1]);
				Double y = Double.parseDouble(waitingLobbyStrSplit[2]);
				Double z = Double.parseDouble(waitingLobbyStrSplit[3]);
				Float yaw = Float.parseFloat(waitingLobbyStrSplit[4]);
				Float pitch = Float.parseFloat(waitingLobbyStrSplit[5]);
				this.waitingLobby = new Location(world, x, y, z, yaw, pitch);
			}
		}

		this.prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));

		this.minPlayers = config.getInt("minPlayers", 5);
		this.maxPlayers = config.getInt("maxPlayers", 20);
		this.gameTimer = config.getInt("gameTimer", 30);
		this.borderHeight = config.getInt("borderHeight", 6);
		this.dropGoldenHead = config.getBoolean("dropGoldenHead", true);
		this.defaultKit = config.getString("defaultKit");
		this.showRunningArenas = config.getBoolean("showRunningArenas", true);
		this.defaultElo = config.getInt("defaultRating", 1200);

		// Load the border stages
		ConfigurationSection borderStagesSC = config.getConfigurationSection("border");
		if (borderStagesSC != null) {
			int i = 0;
			for (String key : borderStagesSC.getKeys(false)) {
				ConfigurationSection borderStageSC = borderStagesSC.getConfigurationSection(key);
				Integer size = borderStageSC.getInt("size", 45);
				Integer time = borderStageSC.getInt("time", 30);
				ArrayList<Integer> messages = (ArrayList<Integer>) borderStageSC.getIntegerList("messages");
				this.borderStages.add(new BorderStage(i, size, time, messages));
				i++;
			}
		}

		ConfigurationSection lobbyItemsSlotsSC = config.getConfigurationSection("lobbyItemsSlots");
		for (String key : lobbyItemsSlotsSC.getKeys(false)) {
			this.lobbyItemsSlots.put(key, lobbyItemsSlotsSC.getInt(key));
		}

		List<String> goldenHeadEffects_ = config.getStringList("goldenHeadEffects");
		for (String effectStr : goldenHeadEffects_) {
			String[] effectStrSplit = effectStr.split(",");
			PotionEffectType pet = PotionEffectType.getByName(effectStrSplit[0]);
			if (pet == null) {
				plugin.getLogger().info("Golden head effect '" + effectStrSplit[0] + "' is invalid");
				continue;
			}
			goldenHeadEffects.add(new PotionEffect(pet, 20 * Integer.parseInt(effectStrSplit[1]), Integer.parseInt(effectStrSplit[2]) - 1));
		}

		List<String> noCleanEffects_ = config.getStringList("noCleanEffects");
		for (String effectStr : noCleanEffects_) {
			String[] effectStrSplit = effectStr.split(",");
			PotionEffectType pet = PotionEffectType.getByName(effectStrSplit[0]);
			if (pet == null) {
				plugin.getLogger().info("No clean effect '" + effectStrSplit[0] + "' is invalid");
				continue;
			}
			noCleanEffects.add(new PotionEffect(pet, 20 * Integer.parseInt(effectStrSplit[1]), Integer.parseInt(effectStrSplit[2]) - 1));
		}

		if (config.getConfigurationSection("mysql") != null) {
			ConfigurationSection mysqlSC = config.getConfigurationSection("mysql");
			this.mysql_host = mysqlSC.getString("host", "");
			this.mysql_port = mysqlSC.getString("port", "");
			this.mysql_database = mysqlSC.getString("database", "");
			this.mysql_username = mysqlSC.getString("username", "");
			this.mysql_password = mysqlSC.getString("password", "");
			try {
				this.mysql_connection = getNewConnection();
				if (this.mysql_connection != null) {

					String sql = "CREATE TABLE IF NOT EXISTS stats(username VARCHAR(64) NOT NULL, UUID VARCHAR(64) NOT NULL UNIQUE, games_played INT(255) DEFAULT 0, kills INT(255) DEFAULT 0, deaths INT(255) DEFAULT 0, games_won INT(255) DEFAULT 0, last_game_position INT(255) DEFAULT 0, average_position_sum INT(255) DEFAULT 0, average_position DOUBLE(16, 2) DEFAULT 0,ELO INT(255), KDR DOUBLE(16, 2) DEFAULT 0, time_played INT(255) DEFAULT 0)";
					this.mysql_connection.createStatement().execute(sql);

					String sql1 = "CREATE TABLE IF NOT EXISTS selected_kit(username VARCHAR(64) NOT NULL, UUID VARCHAR(64) NOT NULL, selected VARCHAR(64) NOT NULL, PRIMARY KEY (UUID, selected))";
					this.mysql_connection.createStatement().execute(sql1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				this.mysqlEnabled = false;
				return;
			}
		}
	}

	private Connection getNewConnection() {
		try {
			if (this.mysql_connection != null && !this.mysql_connection.isClosed()) {
				return this.mysql_connection;
			}
			Class.forName("com.mysql.jdbc.Driver");
			this.mysql_connection = DriverManager.getConnection("jdbc:mysql://" + this.mysql_host + ":" + this.mysql_port + "/" + this.mysql_database, this.mysql_username, this.mysql_password);
			return this.mysql_connection;
		} catch (Exception e) {
			plugin.getLogger().info("[AderlyonUHCMeetup] Can't connect to database!");
			e.printStackTrace();
			this.mysqlEnabled = false;
		}
		return null;
	}

	public Connection mysql_getConnection() {
		try {
			Boolean status;
			status = this.mysql_checkConnection();
			if (!status) {
				System.out.println("[AderlyonUHCMeetup] Something is wrong with your MySQL server.");
				return null;
			}
			return this.mysql_connection;
		} catch (ClassNotFoundException | SQLException ev) {
			ev.printStackTrace();
			this.mysqlEnabled = false;
			return null;
		}
	}

	public boolean mysql_checkConnection() throws SQLException, ClassNotFoundException {
		if (this.mysql_connection == null || this.mysql_connection.isClosed()) {
			this.mysql_connection = getNewConnection();

			if (this.mysql_connection == null || this.mysql_connection.isClosed()) {
				return false;
			}
		}
		return true;
	}

	public String getPrefix() {
		return prefix;
	}

	public Integer getLobbyItemSlot(String key) {
		Integer out = lobbyItemsSlots.get(key);
		if (out == null) {
			out = 0;
		}
		return out;
	}

	public String getBungeecordLobbyServer() {
		return bungeecordLobbyServer;
	}

	public String getMysql_host() {
		return mysql_host;
	}

	public String getMysql_database() {
		return mysql_database;
	}

	public String getMysql_username() {
		return mysql_username;
	}

	public String getMysql_password() {
		return mysql_password;
	}

	public String getMysql_port() {
		return mysql_port;
	}

	public Connection getMysql_connection() {
		return mysql_connection;
	}

	public String getServerName() {
		return serverName;
	}

	public Location getWaitingLobby() {
		return waitingLobby;
	}

	public void setWaitingLobby(Location waitingLobby) {
		this.waitingLobby = waitingLobby;
	}

	public Integer getMinPlayers() {
		return minPlayers;
	}

	public Integer getMaxPlayers() {
		return maxPlayers;
	}

	public Integer getGameTimer() {
		return gameTimer;
	}

	public ArrayList<BorderStage> getBorderStages() {
		return borderStages;
	}

	public Integer getBorderHeight() {
		return borderHeight;
	}

	public ArrayList<PotionEffect> getGoldenHeadEffects() {
		return goldenHeadEffects;
	}

	public Boolean getDropGoldenHead() {
		return dropGoldenHead;
	}

	public String getDefaultKit() {
		return defaultKit;
	}

	public ArrayList<PotionEffect> getNoCleanEffects() {
		return noCleanEffects;
	}

	public Boolean getMysqlEnabled() {
		return mysqlEnabled;
	}

	public ArrayList<String> getGameServers() {
		return gameServers;
	}

	public Boolean getShowRunningArenas() {
		return showRunningArenas;
	}

	public Integer getDefaultElo() {
		return defaultElo;
	}
}
