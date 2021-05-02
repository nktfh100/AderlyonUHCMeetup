package com.nktfh100.AderlyonUHCMeetup.main;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nktfh100.AderlyonUHCMeetup.commands.MeetupCommand;
import com.nktfh100.AderlyonUHCMeetup.commands.MeetupCommandTab;
import com.nktfh100.AderlyonUHCMeetup.events.*;
import com.nktfh100.AderlyonUHCMeetup.managers.BungeManager;
import com.nktfh100.AderlyonUHCMeetup.managers.ConfigManager;
import com.nktfh100.AderlyonUHCMeetup.managers.EloManager;
import com.nktfh100.AderlyonUHCMeetup.managers.GameManager;
import com.nktfh100.AderlyonUHCMeetup.managers.ItemsManager;
import com.nktfh100.AderlyonUHCMeetup.managers.KitsManager;
import com.nktfh100.AderlyonUHCMeetup.managers.MessagesManager;
import com.nktfh100.AderlyonUHCMeetup.managers.PlayersManager;
import com.nktfh100.AderlyonUHCMeetup.managers.SoundsManager;
import com.nktfh100.AderlyonUHCMeetup.managers.WorldManager;
import com.nktfh100.AderlyonUHCMeetup.utils.compatibility.PluginVersion;
import com.nktfh100.AderlyonUHCMeetup.utils.compatibility.versions.*;

public class UHCMeetup extends JavaPlugin {

	private static UHCMeetup instance;

	private ConfigManager configManager;
	private MessagesManager messagesManager;
	private GameManager gameManager;
	private SoundsManager soundsManager;
	private ItemsManager itemsManager;
	private PlayersManager playersManager;
	private WorldManager worldManager;
	private KitsManager kitsManager;
	private BungeManager bungeManager;
	private EloManager eloManager;
	private PluginVersion versionHandler;

	private Boolean isPlaceHolderAPI = false;

	private Boolean isLobby = false;

	public UHCMeetup() {
		instance = this;
	}

	@Override
	public void onEnable() {
		getLogger().info("Loading Aderlyon UHC Meetup...");

		checkSpigotVersion();

		configManager = new ConfigManager(this);
		configManager.loadConfig();

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		messagesManager = new MessagesManager(this);
		itemsManager = new ItemsManager(this);
		soundsManager = new SoundsManager(this);
		kitsManager = new KitsManager(this);
		messagesManager.loadAll();
		itemsManager.loadItems();
		soundsManager.loadSounds();
		kitsManager.loadKits();
		playersManager = new PlayersManager(this);
		worldManager = new WorldManager(this);
		gameManager = new GameManager(this);
		bungeManager = new BungeManager(this, configManager.getGameServers());
		eloManager = new EloManager(this);
		if (isLobby) {
			getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeManager);
		}

		if (versionHandler != null && !isLobby) {
			versionHandler.biomeSwapper();
		}

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(playersManager, this);
		if (!isLobby) {
			pluginManager.registerEvents(new PlayerDeath(), this);
			pluginManager.registerEvents(new PlayerDamage(), this);
			pluginManager.registerEvents(new PlayerSwapHand(), this);
			pluginManager.registerEvents(new ChunkLoad(), this);
			pluginManager.registerEvents(new CreatureSpawn(), this);
			pluginManager.registerEvents(new InvDrag(), this);
			pluginManager.registerEvents(new PlayerRegainHealth(), this);
			pluginManager.registerEvents(new PortalCreate(), this);
			pluginManager.registerEvents(new PlayerChat(), this);
			pluginManager.registerEvents(new PlayerCraftItem(), this);
			pluginManager.registerEvents(new PlayerRespawn(), this);
			pluginManager.registerEvents(new ServerListPing(), this);
			pluginManager.registerEvents(new PlayerDamageOther(), this);
			pluginManager.registerEvents(new PlayerPickUp(), this);
			pluginManager.registerEvents(new PlayerBlockBreak(), this);
			pluginManager.registerEvents(new PlayerBlockPlace(), this);
			pluginManager.registerEvents(new PlayerDrop(), this);
			pluginManager.registerEvents(new WeatherChange(), this);
			pluginManager.registerEvents(new HungerChange(), this);
		}
		pluginManager.registerEvents(new InvClose(), this);
		pluginManager.registerEvents(new InvClick(), this);
		pluginManager.registerEvents(new PlayerInteract(), this);

		this.getCommand("uhcmeetup").setExecutor(new MeetupCommand());
		this.getCommand("uhcmeetup").setTabCompleter(new MeetupCommandTab());

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			isPlaceHolderAPI = true;
			new SomeExpansion(this).register();
		}
		if (!isLobby) {
			worldManager.generateNewWorld();
		}
//		new Metrics(this, 10816); TODO

	}

	private void checkSpigotVersion() {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		boolean isVaild = true;
		if ("v1_8_R3".equals(version)) {
			versionHandler = new V1_8_R3();
		} else if ("v1_9_R1".equals(version)) {
			versionHandler = new V1_9_R1();
		} else if ("v1_10_R1".equals(version)) {
			versionHandler = new V1_10_R1();
		} else if ("v1_11_R1".equals(version)) {
			versionHandler = new V1_11_R1();
		} else if ("v1_12_R1".equals(version)) {
			versionHandler = new V1_12_R1();
		} else if ("v1_13_R1".equals(version)) {
			versionHandler = new V1_13_R1();
		} else if ("v1_13_R2".equals(version)) {
			versionHandler = new V1_13_R2();
		} else if ("v1_14_R1".equals(version)) {
			versionHandler = new V1_14_R1();
		} else if ("v1_15_R1".equals(version)) {
			versionHandler = new V1_15_R1();
		} else if ("v1_16_R1".equals(version)) {
			versionHandler = new V1_16_R1();
		} else if ("v1_16_R2".equals(version)) {
			versionHandler = new V1_16_R2();
		} else if ("v1_16_R3".equals(version)) {
			versionHandler = new V1_16_R3();
		} else {
			isVaild = false;
		}
		if (!isVaild) {
			UHCMeetup.getInstance().getLogger().info("Unsupported Version! (only for 1.8.8 ~ 1.16.5)");
			Bukkit.shutdown();
		}
	}

	public void sendPlayerToLobby(Player player) {
		if (!isEnabled()) {
			return;
		}
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(configManager.getBungeecordLobbyServer());
		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}

	public void sendAllToLobby() {

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(configManager.getBungeecordLobbyServer());

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
		}
	}

	public void sendPlayerToArena(Player player, String server) {
		if (!isEnabled()) {
			return;
		}
		if (isLobby) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(server);
			player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
		}
	}

	// Send an update to the lobby server
	// Only works if there is at least one player online
	public void sendBungeUpdate() {
		try {
			if (!isEnabled() || isLobby) {
				return;
			}
			Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
			if (player == null) {
				return;
			}

			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);

			String str = getConfigManager().getServerName() + "," + getGameManager().getGameState().toString() + "," + getGameManager().getPlayers().size() + "," + getConfigManager().getMaxPlayers();

			out.writeUTF("Forward");
			out.writeUTF(getConfigManager().getBungeecordLobbyServer());
			out.writeUTF("UHCMeetup");
			byte[] data = str.getBytes();
			out.writeShort(data.length);
			out.write(data);

			player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public MessagesManager getMessagesManager() {
		return messagesManager;
	}

	public Boolean getIsPlaceHolderAPI() {
		return isPlaceHolderAPI;
	}

	public SoundsManager getSoundsManager() {
		return soundsManager;
	}

	public ItemsManager getItemsManager() {
		return itemsManager;
	}

	public PlayersManager getPlayersManager() {
		return playersManager;
	}

	public WorldManager getWorldManager() {
		return worldManager;
	}

	public PluginVersion getVersionHandler() {
		return versionHandler;
	}

	public KitsManager getKitsManager() {
		return kitsManager;
	}

	public BungeManager getBungeManager() {
		return bungeManager;
	}

	public Boolean getIsLobby() {
		return isLobby;
	}

	public void setIsLobby(Boolean is) {
		this.isLobby = is;
	}

	public static UHCMeetup getInstance() {
		return instance;
	}

	public EloManager getEloManager() {
		return eloManager;
	}

}