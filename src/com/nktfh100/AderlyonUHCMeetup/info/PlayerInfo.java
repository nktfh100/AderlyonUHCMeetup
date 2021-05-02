package com.nktfh100.AderlyonUHCMeetup.info;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.enums.StatDouble;
import com.nktfh100.AderlyonUHCMeetup.enums.StatInt;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.managers.MessagesManager;
import com.nktfh100.AderlyonUHCMeetup.managers.StatsManager;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;

public class PlayerInfo {

	private Player player;
	private String selectedKit = null;
	private Boolean isSpectating = false;
	private Scoreboard board;
	private Objective objective;
	private Objective objectiveHealth;

	private int gameKills = 0;

	private StatsManager statsManager;

	public PlayerInfo(Player player) {
		this.player = player;

		this.statsManager = new StatsManager(this);
	}

	public void _setPlayer(Player p) {
		this.player = p;
	}

	@SuppressWarnings("deprecation")
	public void _setWaitingLobbyScoreboard() {
		this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		this.board = null;
		this.objective = null;
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = this.board.registerNewObjective(this.player.getName(), "dummy");
		this.objective.setDisplayName(UHCMeetup.getInstance().getMessagesManager().getScoreboard("title"));
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.player.setScoreboard(this.board);
		this.setScoreboard(false);
	}

	public Player getPlayer() {
		return this.player;
	}

	public void addKills(int amount) {
		this.gameKills = this.gameKills + amount;
	}

	public void setKills(int amount) {
		this.gameKills = amount;
	}

	public int getKills() {
		return this.gameKills;
	}

	@SuppressWarnings("deprecation")
	public void gameStarted() {

		this.board = null;
		this.objective = null;
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = this.board.registerNewObjective(this.player.getName(), "dummy");
		this.objective.setDisplayName(UHCMeetup.getInstance().getMessagesManager().getScoreboard("title"));
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objectiveHealth = this.board.registerNewObjective("showhealth", Criterias.HEALTH);
		this.objectiveHealth.setDisplayName(ChatColor.RED + "\u2764");
		this.objectiveHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
		this.setScoreboard(true);

		this.player.setScoreboard(this.board);
	}

	@SuppressWarnings("deprecation")
	public void gameEnded() {

		this.board = null;
		this.objective = null;
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = this.board.registerNewObjective(this.player.getName(), "dummy");
		this.objective.setDisplayName(UHCMeetup.getInstance().getMessagesManager().getScoreboard("title"));
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objectiveHealth = this.board.registerNewObjective("showhealth", Criterias.HEALTH);
		this.objectiveHealth.setDisplayName(ChatColor.RED + "\u2764");
		this.objectiveHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
		this.setScoreboard(true);

		this.player.setScoreboard(this.board);
	}

	@SuppressWarnings("deprecation")
	public void playerDied() {

		this.statsManager.setStatInt(StatInt.DEATHS, this.statsManager.getStatInt(StatInt.DEATHS) + 1);
		this.statsManager.setStatDouble(StatDouble.KDR, (double) this.statsManager.getStatInt(StatInt.KILLS) / (double) this.statsManager.getStatInt(StatInt.DEATHS));

		int playerPos = UHCMeetup.getInstance().getGameManager().getPlayersAlive().size();
		this.getStatsManager().setStatInt(StatInt.LAST_GAME_POSITION, playerPos);

		this.getStatsManager().setStatInt(StatInt.AVERAGE_POSITION_SUM, this.getStatsManager().getStatInt(StatInt.AVERAGE_POSITION_SUM) + playerPos);
		this.getStatsManager().setStatDouble(StatDouble.AVERAGE_POSITION,
				(double) this.getStatsManager().getStatInt(StatInt.AVERAGE_POSITION_SUM) / (double) this.getStatsManager().getStatInt(StatInt.GAMES_PLAYED));

		this.isSpectating = true;

		this.board = null;
		this.objective = null;
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = this.board.registerNewObjective(this.player.getName(), "dummy");
		this.objective.setDisplayName(UHCMeetup.getInstance().getMessagesManager().getScoreboard("title"));
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objectiveHealth = this.board.registerNewObjective("showhealth", Criterias.HEALTH);
		this.objectiveHealth.setDisplayName(ChatColor.RED + "\u2764");
		this.objectiveHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
		this.setScoreboard(true);

		this.player.setScoreboard(this.board);

	}

	private String activeKey = "";

	private void setScoreboard(Boolean gameRunning) {
		MessagesManager messagesManager = UHCMeetup.getInstance().getMessagesManager();
		this.activeKey = this.getScoreBoardKey(gameRunning);

		int score = 99;
		for (int i = 0; i < messagesManager.getScoreBoardLines(this.activeKey).size(); i++) {
			Team team_ = this.registerTeam(score);
			String line = messagesManager.getScoreboardLine(this.activeKey, i, this);

			if (line.length() > 16) {
				final int mid = line.length() / 2;
				String part1 = line.substring(0, mid);
				String part2 = line.substring(mid);

				String part2Color = ChatColor.WHITE + "";
				// Bug that could happen:
				// If line splits at half exactly at the '§' symbol
				// Will cause error because part1.charAt(lastColorIndex + 1) wouldn't exist
				Integer lastColorIndex = part1.lastIndexOf('§');
				if (lastColorIndex != -1) {
					if (part1.substring(part1.length() - 1).equals("§")) {
						part2Color = "§" + part2.charAt(0);
						part2 = part2.substring(1);
						part1 = part1.substring(0, part1.length() - 1);
					} else {
						part2Color = "§" + part1.charAt(lastColorIndex + 1);
					}
				}

				team_.setPrefix(part1);
				team_.setSuffix(part2Color + part2);
			} else {
				team_.setPrefix(line);
			}
			score--;
		}

	}

	public void updateScoreboard() {
		if (this.board == null) {
			return;
		}
		MessagesManager messagesManager = UHCMeetup.getInstance().getMessagesManager();

		int score = 99;
		for (int i = 0; i < messagesManager.getScoreBoardLines(this.activeKey).size(); i++) {
			if (this.board.getTeam("team" + score) == null) {
				this.registerTeam(score);
			}
			String line = messagesManager.getScoreboardLine(this.activeKey, i, this);
			Team team_ = this.board.getTeam("team" + score);
			if (line.length() > 16) {
				final int mid = line.length() / 2;
				String part1 = line.substring(0, mid);
				String part2 = line.substring(mid);

				String part2Color = ChatColor.WHITE + "";
				Integer lastColorIndex = part1.lastIndexOf('§');
				if (lastColorIndex != -1) {
					if (part1.substring(part1.length() - 1).equals("§")) {
						part2Color = "§" + part2.charAt(0);
						part2 = part2.substring(1);
						part1 = part1.substring(0, part1.length() - 1);
					} else {
						part2Color = "§" + part1.charAt(lastColorIndex + 1);
					}
				}

				team_.setPrefix(part1);
				team_.setSuffix(part2Color + part2);
			} else {
				team_.setPrefix(line);
			}
			score--;
		}

		if (activeKey != this.getScoreBoardKey()) {
			this.activeKey = this.getScoreBoardKey();
			this.updateScoreboard();
		}
	}

	private String getScoreBoardKey() {
		if (UHCMeetup.getInstance().getGameManager().getGameState() == GameState.RUNNING) {
			return getScoreBoardKey(true);
		} else {
			return getScoreBoardKey(false);
		}
	}

	private String getScoreBoardKey(Boolean gameRunning) {
		if (gameRunning) {
			if (this.isSpectating) {
				return "spectator";
			} else {
				return "playing";
			}
		} else {
			return "waiting-lobby";
		}
	}

	private Team registerTeam(int score) {
		Team team_ = this.board.registerNewTeam("team" + score);
		String entry = Utils.getRandomColors();
		team_.addEntry(entry);
		this.objective.getScore(entry).setScore(score);
		return team_;
	}

	public void leaveGame() {

		if (this.board != null) {
			for (Team team : this.board.getTeams()) {
				for (String entry : team.getEntries()) {
					team.removeEntry(entry);
					this.board.resetScores(entry);
				}
				team.unregister();
			}
		}
		if (this.objective != null) {
			this.objective.unregister();
		}
		if (this.objectiveHealth != null) {
			this.objectiveHealth.unregister();
		}
		this.board = null;
		this.objective = null;
		this.objectiveHealth = null;
		this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

		this.gameKills = 0;
		this.setSpectating(false);
	}

	public void setKit(String name) {
		this.selectedKit = name;
	}

	public String getKit() {
		return this.selectedKit;
	}

	public Boolean isSpectating() {
		return this.isSpectating;
	}

	public void setSpectating(Boolean is) {
		this.isSpectating = is;
	}

	public StatsManager getStatsManager() {
		return statsManager;
	}
}
