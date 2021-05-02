package com.nktfh100.AderlyonUHCMeetup.managers;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.enums.ScenarioType;
import com.nktfh100.AderlyonUHCMeetup.enums.StatDouble;
import com.nktfh100.AderlyonUHCMeetup.enums.StatInt;
import com.nktfh100.AderlyonUHCMeetup.info.BorderStage;
import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.inventory.ScenarioVoteInv;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;
import com.nktfh100.AderlyonUHCMeetup.utils.XMaterial;

public class GameManager {

	private UHCMeetup plugin;

	private ArrayList<PlayerInfo> playersIngame = new ArrayList<PlayerInfo>();
	private ArrayList<PlayerInfo> playersAlive = new ArrayList<PlayerInfo>();
	private ArrayList<PlayerInfo> spectators = new ArrayList<PlayerInfo>();
	private GameState gameState = GameState.LOADING;
	private ScenarioType activeScenario = ScenarioType.DEFAULT;

	private BukkitTask borderRunnable = null;
	private BukkitTask gameTimerRunnable = null;

	private BorderStage activeBorderStage;

	private Boolean gameFinished = false;
	private Integer gameTimer = 30;
	private Integer timeToNextBorder = 0; // In seconds
	private Integer gameTime = 0; // Fow how long the game have been running in seconds

	private ScenarioVoteInv scenarioVoteInv;

	public GameManager(UHCMeetup instance) {
		this.plugin = instance;
		this.scenarioVoteInv = new ScenarioVoteInv();
	}

	@SuppressWarnings("deprecation")
	public void startGame() {
		if (this.gameState == GameState.RUNNING || this.gameState == GameState.LOADING) {
			return;
		}
		this.gameState = GameState.RUNNING;
		if (this.borderRunnable != null) {
			this.borderRunnable.cancel();
			this.borderRunnable = null;
		}
		if (this.gameTimerRunnable != null) {
			this.gameTimerRunnable.cancel();
			this.gameTimerRunnable = null;
		}
		this.activeBorderStage = this.plugin.getConfigManager().getBorderStages().get(0);
		this.timeToNextBorder = this.activeBorderStage.getTime();

		final Integer borderSize = this.activeBorderStage.getSize();
		final World uhcWorld = this.plugin.getWorldManager().getWorld();
		if (uhcWorld == null) {
			return;
		}
		// Get the most voted scenario
		Integer highestVotes = 0;
		ScenarioType mostVotes = ScenarioType.DEFAULT;
		for (ScenarioType scenario_ : this.scenarioVoteInv.getVotes().keySet()) {
			int votes = this.scenarioVoteInv.getVotes().get(scenario_);
			if (votes > highestVotes) {
				mostVotes = scenario_;
				highestVotes = votes;
			}
		}
		this.activeScenario = mostVotes;
		this.playersAlive = new ArrayList<PlayerInfo>();
		for (PlayerInfo pInfo : this.playersIngame) {
			Player player = pInfo.getPlayer();
			this.playersAlive.add(pInfo);
			pInfo.gameStarted();
			// Teleport players to a random location inside the border
			// Try to find a safe location without water/lava
			// If can't find one - teleport anyway
			Location loc = null;
			for (int ii = 0; ii < 10; ii++) {
				Integer x_ = Utils.getRandomNumberInRange(-borderSize + 5, borderSize - 5);
				Integer z_ = Utils.getRandomNumberInRange(-borderSize + 5, borderSize - 5);
				Block block_ = uhcWorld.getHighestBlockAt(x_, z_);
				loc = new Location(uhcWorld, x_, block_.getY() + 1, z_);
				if (block_.getType() != XMaterial.WATER.parseMaterial() && block_.getType() != XMaterial.LAVA.parseMaterial()) {
					break;
				}
			}
			if (loc != null) {
				player.teleport(loc);
			}
			for (PotionEffect pe : player.getActivePotionEffects()) {
				player.removePotionEffect(pe.getType());
			}
			player.setExp(0);
			player.setLevel(0);
			player.setHealth(pInfo.getPlayer().getMaxHealth());
			player.setFoodLevel(40);
			player.setGameMode(GameMode.SURVIVAL);
			player.setAllowFlight(false);
			player.setFlying(false);
			if (pInfo.getKit() != null && this.plugin.getKitsManager().getKit(pInfo.getKit()) != null) {
				this.plugin.getKitsManager().getKit(pInfo.getKit()).giveKit(player);
			} else {
				this.plugin.getKitsManager().getDefaultKit().giveKit(player);
			}
		}
		for (PlayerInfo pInfo : this.playersIngame) {
			Player player = pInfo.getPlayer();
			pInfo.updateScoreboard();
			player.setExp(0f);
			player.setLevel(0);
			player.setHealth(player.getHealth());
			pInfo.getStatsManager().setStatInt(StatInt.GAMES_PLAYED, pInfo.getStatsManager().getStatInt(StatInt.GAMES_PLAYED) + 1);
		}
		Bukkit.broadcastMessage(this.plugin.getMessagesManager().getGameMsg("scenarioChosen", this.plugin.getMessagesManager().getScenarioName(this.activeScenario.getName())));
		this.startBorderTimer();
		this.startBorderCheckTask();
		this.startOneSecondTimer();
		new BukkitRunnable() {

			@Override
			public void run() {
				// To fix the hearts below name showing 0
				for (PlayerInfo pInfo : getPlayers()) {
					pInfo.getPlayer().setHealth(pInfo.getPlayer().getHealth());
				}
			}
		}.runTaskLater(this.plugin, 20L * 3);
	}

	public void endGame(Player winner) {
		this.gameFinished = true;
		String winnerName = winner != null ? winner.getName() : "";
		for (String line : this.plugin.getMessagesManager().getGameMsg("winMessage", winnerName).split("/n")) {
			Bukkit.broadcastMessage(line);
		}
		this.borderRunnable.cancel();
		PlayerInfo winnerInfo = this.plugin.getPlayersManager().getPlayerInfo(winner);
		if (winnerInfo != null) {
			winnerInfo.getStatsManager().setStatInt(StatInt.GAMES_WON, winnerInfo.getStatsManager().getStatInt(StatInt.GAMES_WON) + 1);

			int playerPos = 1;
			winnerInfo.getStatsManager().setStatInt(StatInt.LAST_GAME_POSITION, playerPos);

			winnerInfo.getStatsManager().setStatInt(StatInt.AVERAGE_POSITION_SUM, winnerInfo.getStatsManager().getStatInt(StatInt.AVERAGE_POSITION_SUM) + playerPos);
			winnerInfo.getStatsManager().setStatDouble(StatDouble.AVERAGE_POSITION,
					(double) winnerInfo.getStatsManager().getStatInt(StatInt.AVERAGE_POSITION_SUM) / (double) winnerInfo.getStatsManager().getStatInt(StatInt.GAMES_PLAYED));
		}
		this.plugin.getEloManager().calcNewRatings();
		for (PlayerInfo pInfo_ : this.getPlayers()) {
			this.plugin.getSoundsManager().playSound("playerWin", pInfo_.getPlayer());
			pInfo_.getStatsManager().saveStats(true);
			pInfo_.gameEnded();
		}
		GameManager gameManager = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				gameManager.setGameState(GameState.LOADING);
				plugin.sendBungeUpdate();
				// Send players to the lobby server
				plugin.sendAllToLobby();
				new BukkitRunnable() {
					@Override
					public void run() {
						Bukkit.unloadWorld("uhc", false);
						plugin.getWorldManager().deleteDirectory(new File("uhc"));
						Bukkit.shutdown();
					}
				}.runTaskLater(plugin, 20L * 20L);
			}
		}.runTaskLater(this.plugin, 20L * 10L);
	}

	public void playerJoin(Player player) {
		if (this.gameState == GameState.WAITING || this.gameState == GameState.STARTING) {
			PlayerInfo pInfo = this.plugin.getPlayersManager().getPlayerInfo(player);
			pInfo._setWaitingLobbyScoreboard();
			this.playersIngame.add(pInfo);

			for (PotionEffect pe : player.getActivePotionEffects()) {
				player.removePotionEffect(pe.getType());
			}
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);

			player.getInventory().setItem(this.plugin.getConfigManager().getLobbyItemSlot("stats"), this.plugin.getItemsManager().getItem("stats").getItem().getItem());
			player.getInventory().setItem(this.plugin.getConfigManager().getLobbyItemSlot("kitSelector"), this.plugin.getItemsManager().getItem("kitSelector").getItem().getItem());
			player.getInventory().setItem(this.plugin.getConfigManager().getLobbyItemSlot("leave"), this.plugin.getItemsManager().getItem("leave").getItem().getItem());
			player.getInventory().setItem(this.plugin.getConfigManager().getLobbyItemSlot("scenarioVote"), this.plugin.getItemsManager().getItem("scenarioVote").getItem().getItem());

			this.updateScoreboard();

			if (this.gameState == GameState.WAITING && this.playersIngame.size() >= this.plugin.getConfigManager().getMinPlayers()) {
				this.startTimer();
			}
			this.plugin.sendBungeUpdate();
		} else {
			player.sendMessage(ChatColor.RED + "You can't join the game right now!");
		}
	}

	public void playerLeave(PlayerInfo pInfo) {
		if (this.gameState == GameState.RUNNING) {
			if (this.playersAlive.size() <= 1) {
				Player winner = null;
				if (this.playersAlive.size() >= 1) {
					winner = this.playersAlive.get(0).getPlayer();
				}
				this.endGame(winner);
			}
			pInfo.getPlayer().damage(1000);
		} else if (this.gameState == GameState.STARTING) {
			if (this.getPlayers().size() < this.plugin.getConfigManager().getMinPlayers()) {
				this.gameTimerRunnable.cancel();
				Bukkit.broadcastMessage(this.plugin.getMessagesManager().getGameMsg("notEnoughPlayers"));
			}
		}
		this.playersIngame.remove(pInfo);
		this.playersAlive.remove(pInfo);
		this.spectators.remove(pInfo);
		this.updateScoreboard();
		this.plugin.sendBungeUpdate();
	}

	@SuppressWarnings("deprecation")
	public void playerDeath(Player player, Player killer, Integer distance) {
		PlayerInfo pInfo = this.plugin.getPlayersManager().getPlayerInfo(player);
		pInfo.playerDied();
		player.setGameMode(GameMode.SPECTATOR);
		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
		player.setFoodLevel(40);
		player.setHealth(player.getMaxHealth());

		for (PlayerInfo pInfo_ : this.getPlayers()) {
			if (pInfo_.getPlayer() != player) {
				pInfo_.getPlayer().hidePlayer(player);
			}
		}

		if (killer != null) {
			PlayerInfo killerInfo = this.plugin.getPlayersManager().getPlayerInfo(killer);
			killerInfo.addKills(1);
			killerInfo.getStatsManager().setStatInt(StatInt.KILLS, killerInfo.getStatsManager().getStatInt(StatInt.KILLS) + 1);
			Double kdr = (double) killerInfo.getStatsManager().getStatInt(StatInt.KILLS) / (double) killerInfo.getStatsManager().getStatInt(StatInt.DEATHS);

			if (kdr.isInfinite()) { // Deaths = 0
				killerInfo.getStatsManager().setStatDouble(StatDouble.KDR, (double) killerInfo.getStatsManager().getStatInt(StatInt.KILLS));
			}
			if (kdr.isNaN()) {
				kdr = 0D;
			}
			killerInfo.getStatsManager().setStatDouble(StatDouble.KDR, kdr);
			if (this.activeScenario == ScenarioType.NOCLEAN) {
				for (PotionEffect pe : this.plugin.getConfigManager().getNoCleanEffects()) {
					killer.addPotionEffect(pe);
				}
			}
		}

		this.playersAlive.remove(pInfo);
		this.spectators.add(pInfo);

		// Death message
		EntityDamageEvent damageEvent = player.getLastDamageCause();
		EntityDamageEvent.DamageCause damageCause = damageEvent.getCause();
		String msg = this.plugin.getMessagesManager().getDeathMsg(Utils.getDeathMsgType(player, killer, damageCause), player, killer, distance);
		Bukkit.broadcastMessage(msg);

		this.updateScoreboard();

		if (this.playersAlive.size() <= 1) {
			Player winner = null;
			if (this.playersAlive.size() >= 1) {
				winner = this.playersAlive.get(0).getPlayer();
			}
			this.endGame(winner);
		}
		this.plugin.sendBungeUpdate();
	}

	public void startOneSecondTimer() {
		// Runs every second
		GameManager gameManager = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (gameManager.getGameState() != GameState.RUNNING) {
					this.cancel();
				}
				gameManager.setGameTime(gameManager.getGameTime() + 1);
				gameManager.updateScoreboard();
				for (PlayerInfo pInfo : gameManager.getPlayers()) {
					pInfo.getStatsManager().setStatInt(StatInt.TIME_PLAYED, pInfo.getStatsManager().getStatInt(StatInt.TIME_PLAYED) + 1);
				}
			}
		}.runTaskTimer(this.plugin, 20L, 20L);
	}

	// Timer before starting the game
	public void startTimer() {
		if (this.gameState == GameState.STARTING) {
			return;
		}

		this.gameState = GameState.STARTING;
		GameManager gameManager = this;
		this.gameTimerRunnable = new BukkitRunnable() {
			Integer gameTimer_ = gameManager.getGameTimer();

			@Override
			public void run() {
				if (gameManager.getGameState() != GameState.STARTING || gameManager.getPlayers().size() < plugin.getConfigManager().getMinPlayers()) {
					this.cancel();
					return;
				}

				for (PlayerInfo pInfo : gameManager.getPlayers()) {
					Player player = pInfo.getPlayer();
					if ((gameTimer_ >= 0 && gameTimer_ <= 5) || gameTimer_ == 10 || gameTimer_ == 20 || gameTimer_ == 30 || gameTimer_ == 60) {
						plugin.getSoundsManager().playSound("gameTimerTick", player);
						player.sendMessage(plugin.getMessagesManager().getGameMsg("gameStartingTime", gameTimer_ + ""));
						if (gameTimer_ <= 0) {
							player.sendMessage(plugin.getMessagesManager().getGameMsg("gameStarting"));
						}
					}
					if (gameTimer_ >= 0) {
						pInfo.getPlayer().setLevel(gameTimer_);
						pInfo.updateScoreboard();
					}
				}
				gameTimer_--;
				gameManager.setGameTimer(gameTimer_);
				if (gameTimer_ <= 0) {
					gameManager.startGame();
					this.cancel();
					return;
				}

			}
		}.runTaskTimer(this.plugin, 0L, 20L);
		this.plugin.sendBungeUpdate();
	}

	public void startBorderTimer() {
		this.timeToNextBorder = this.activeBorderStage.getTime();
		GameManager gameManager = this;
		this.borderRunnable = new BukkitRunnable() {

			@Override
			public void run() {
				if (gameManager.getGameState() != GameState.RUNNING) {
					this.cancel();
					return;
				}
				if (gameManager.getActiveBorderStage().getId() + 1 >= plugin.getConfigManager().getBorderStages().size()) {
					// There isn't another border stage after this one
					gameManager.setTimeToNextBorder(0);
					gameManager.updateScoreboard();
					this.cancel();
					return;
				}
				plugin.getConfigManager().getBorderStages().get(gameManager.getActiveBorderStage().getId());
				Integer timer_ = gameManager.getTimeToNextBorder();
				Boolean sendMsg = gameManager.getActiveBorderStage().getMessages().contains(timer_);
				final Integer newBorderSize = plugin.getConfigManager().getBorderStages().get(gameManager.getActiveBorderStage().getId() + 1).getSize();
				for (PlayerInfo pInfo : gameManager.getPlayers()) {
					Player player = pInfo.getPlayer();
					if (sendMsg) {
						plugin.getSoundsManager().playSound("borderTimerTick", player);
						player.sendMessage(plugin.getMessagesManager().getGameMsg("borderWillShrink", timer_ + "", newBorderSize + ""));
					}
					if (timer_ >= 0) {
						pInfo.updateScoreboard();
					}
				}

				timer_--;
				gameManager.setTimeToNextBorder(timer_);
				if (timer_ <= 0) {
					gameManager.nextBorder();
					return;
				}
			}
		}.runTaskTimer(this.plugin, 20L, 20L);
	}

	public void nextBorder() {
		if (this.getActiveBorderStage().getId() + 1 >= this.plugin.getConfigManager().getBorderStages().size()) {
			// There isn't another border stage after this one
			return;
		}

		this.activeBorderStage = this.plugin.getConfigManager().getBorderStages().get(this.getActiveBorderStage().getId() + 1);
		this.timeToNextBorder = this.activeBorderStage.getTime();

		Bukkit.broadcastMessage(this.plugin.getMessagesManager().getGameMsg("borderShrunk", this.activeBorderStage.getSize() + ""));

		// teleport players outside the old border to inside the new one
		for (PlayerInfo pInfo : this.getPlayers()) {

			this.plugin.getSoundsManager().playSound("borderShrunk", pInfo.getPlayer());

			this.activeBorderStage.teleportPlayerInside(pInfo.getPlayer());
		}

		this.createBorder(this.activeBorderStage.getSize());
	}

	// Create the bedrock border
	public void createBorder(Integer size) {
		if (!this.plugin.getWorldManager().getIsFinished() || this.plugin.getWorldManager().getWorld() == null) {
			return;
		}
		World world = this.plugin.getWorldManager().getWorld();
		Location corner1 = new Location(world, size, 0, size);
		Location corner2 = new Location(world, -size, 0, -size);
		Material mat = XMaterial.BEDROCK.parseMaterial();
		for (Location blockLoc : Utils.getBorderBlocks(corner1, corner2, this.plugin.getConfigManager().getBorderHeight())) {
			if (!blockLoc.getChunk().isLoaded()) {
				blockLoc.getChunk().load();
			}
			blockLoc.getBlock().setType(mat, false);
		}
	}

	public void startBorderCheckTask() {
		GameManager gameManager = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (gameManager.getGameState() != GameState.RUNNING) {
					this.cancel();
					return;
				}
				BorderStage borderStage = gameManager.getActiveBorderStage();
				for (PlayerInfo pInfo : gameManager.getPlayers()) {
					Location pLoc = pInfo.getPlayer().getLocation();
					if (!borderStage.insideBorder(pLoc.getX(), pLoc.getZ())) {
						// Get a location inside the border
						double xLoc = pLoc.getX();
						double zLoc = pLoc.getZ();
						double yLoc = pLoc.getY();

						if (xLoc <= borderStage.getMinX())
							xLoc = borderStage.getMaxX() - 3;
						else if (xLoc >= borderStage.getMaxX())
							xLoc = borderStage.getMinX() + 3;
						if (zLoc <= borderStage.getMinZ())
							zLoc = borderStage.getMaxZ() - 3;
						else if (zLoc >= borderStage.getMaxZ())
							zLoc = borderStage.getMinZ() + 3;
						// 'Launch' the player in that direction
						double x = xLoc - pLoc.getX();
						double y = yLoc - pLoc.getY();
						double z = zLoc - pLoc.getZ();
						Vector playerLookDirection = new Vector(x, y, z);
						pInfo.getPlayer().setVelocity(playerLookDirection.normalize().multiply(1.0));
						pInfo.getPlayer().sendMessage(plugin.getMessagesManager().getGameMsg("reachedBorder"));
					}
				}
			}
		}.runTaskTimer(this.plugin, 3L, 3L);
	}

	public void updateScoreboard() {
		for (PlayerInfo pInfo : this.playersIngame) {
			pInfo.updateScoreboard();
		}
	}

	public GameState getGameState() {
		return gameState;
	}

	public Integer getGameTimer() {
		return gameTimer;
	}

	public void setGameTimer(Integer to) {
		this.gameTimer = to;
	}

	public ArrayList<PlayerInfo> getPlayers() {
		return playersIngame;
	}

	public ArrayList<PlayerInfo> getPlayersAlive() {
		return playersAlive;
	}

	public ArrayList<PlayerInfo> getSpectators() {
		return spectators;
	}

	public BorderStage getActiveBorderStage() {
		return activeBorderStage;
	}

	public Integer getGameTime() {
		return gameTime;
	}

	public void setGameTime(Integer gameTime) {
		this.gameTime = gameTime;
	}

	public Integer getTimeToNextBorder() {
		return timeToNextBorder;
	}

	public void setTimeToNextBorder(Integer timeToNextBorder) {
		this.timeToNextBorder = timeToNextBorder;
	}

	public void setGameState(GameState to) {
		this.gameState = to;
	}

	public Boolean getGameFinished() {
		return gameFinished;
	}

	public ScenarioVoteInv getScenarioVoteInv() {
		return scenarioVoteInv;
	}

	public ScenarioType getActiveScenario() {
		return activeScenario;
	}

	public void setActiveScenario(ScenarioType activeScenario) {
		this.activeScenario = activeScenario;
	}

}
