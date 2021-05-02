package com.nktfh100.AderlyonUHCMeetup.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.nktfh100.AderlyonUHCMeetup.enums.StatDouble;
import com.nktfh100.AderlyonUHCMeetup.enums.StatInt;
import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class StatsManager {

	private UHCMeetup plugin;

	private Player player;
	private PlayerInfo pInfo;

	private HashMap<StatInt, Integer> statsInt = new HashMap<StatInt, Integer>();
	private HashMap<StatDouble, Double> statsDouble = new HashMap<StatDouble, Double>();

	private Integer eloChange = 0;

	private Integer leaderboardPositionCache = null;

	public StatsManager(PlayerInfo pInfo) {
		this.plugin = UHCMeetup.getInstance();
		this.pInfo = pInfo;
		this.player = pInfo.getPlayer();
		for (StatInt statInt : StatInt.values()) {
			statsInt.put(statInt, 0);
		}
		for (StatDouble statDouble : StatDouble.values()) {
			statsDouble.put(statDouble, 0D);
		}
	}

	public void loadStats() {
		if (!this.plugin.getConfigManager().getMysqlEnabled()) {
			return;
		}
		StatsManager statsManager = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (pInfo != null) {
					Connection connection = plugin.getConfigManager().getMysql_connection();
					try {
						PreparedStatement ps = connection.prepareStatement("SELECT * FROM stats WHERE UUID = ?");
						ps.setString(1, player.getUniqueId().toString());
						ResultSet rs = ps.executeQuery();
						rs.next();
						for (StatInt statIntE : StatInt.values()) {
							Integer stats_ = rs.getInt(statIntE.getName());
							statsManager.setStatInt(statIntE, stats_ == null ? 0 : stats_);
						}
						for (StatDouble statDoubleE : StatDouble.values()) {
							Double stats_ = rs.getDouble(statDoubleE.getName());
							statsManager.setStatDouble(statDoubleE, stats_ == null ? 0D : stats_);
						}
						if (statsManager.getStatInt(StatInt.ELO) == 0) {
							statsManager.setStatInt(StatInt.ELO, plugin.getConfigManager().getDefaultElo());
						}
						rs.close();
						ps.close();
						statsManager.getpInfo().updateScoreboard();

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(this.plugin);
	}

	public void saveStats(Boolean runAsync) {
		if (!this.plugin.getConfigManager().getMysqlEnabled()) {
			return;
		}
		final String uuid = this.player.getUniqueId().toString();
		final HashMap<StatInt, Integer> statsInt_ = new HashMap<StatInt, Integer>();
		for (StatInt statIntE : StatInt.values()) {
			statsInt_.put(statIntE, this.statsInt.get(statIntE));
		}
		final HashMap<StatDouble, Double> statsDouble_ = new HashMap<StatDouble, Double>();
		for (StatDouble statDoubleE : StatDouble.values()) {
			statsDouble_.put(statDoubleE, this.statsDouble.get(statDoubleE));
		}
		final Integer eloChange = this.eloChange;
		BukkitRunnable runnable = new BukkitRunnable() {

			@Override
			public void run() {
				try {
					PreparedStatement ps = plugin.getConfigManager().getMysql_connection().prepareStatement(
							"UPDATE stats SET games_played=?, kills=?, deaths=?, games_won=?, time_played=?, last_game_position=?, average_position_sum=?, elo=?, average_position=?, kdr=? WHERE UUID=?");
					ps.setInt(1, statsInt_.get(StatInt.GAMES_PLAYED));
					ps.setInt(2, statsInt_.get(StatInt.KILLS));
					ps.setInt(3, statsInt_.get(StatInt.DEATHS));
					ps.setInt(4, statsInt_.get(StatInt.GAMES_WON));
					ps.setInt(5, statsInt_.get(StatInt.TIME_PLAYED));
					ps.setInt(6, statsInt_.get(StatInt.LAST_GAME_POSITION));
					ps.setInt(7, statsInt_.get(StatInt.AVERAGE_POSITION_SUM));
					ps.setInt(8, (int) (statsInt_.get(StatInt.ELO) + eloChange));
					ps.setDouble(9, statsDouble_.get(StatDouble.AVERAGE_POSITION));
					Double kdr = statsDouble_.get(StatDouble.KDR);
					if (kdr.isInfinite()) {
						kdr = statsInt_.get(StatInt.KILLS).doubleValue();
					}
					ps.setDouble(10, kdr);
					ps.setString(11, uuid);
					ps.execute();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		if (runAsync) {
			runnable.runTaskAsynchronously(this.plugin);
		} else {
			runnable.run();
		}
	}

	public void loadSelectedKit() {
		if (!this.plugin.getConfigManager().getMysqlEnabled()) {
			return;
		}
		StatsManager statsManager = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (pInfo != null) {
					Connection connection = plugin.getConfigManager().getMysql_connection();
					try {
						PreparedStatement ps = connection.prepareStatement("SELECT * FROM selected_kit WHERE UUID = ?");
						ps.setString(1, player.getUniqueId().toString());
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							String selected = rs.getString("selected");
							if (plugin.getKitsManager().getKit(selected) == null) {
								selected = plugin.getKitsManager().getDefaultKit().getName();
							}
							pInfo.setKit(selected);
						}
						rs.close();
						ps.close();
						statsManager.getpInfo().updateScoreboard();

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(this.plugin);
	}

	public void saveSelectedKit(String kit) {
		if (!this.plugin.getConfigManager().getMysqlEnabled()) {
			return;
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					PreparedStatement ps = plugin.getConfigManager().getMysql_connection().prepareStatement("UPDATE selected_kit SET selected=? WHERE UUID=?");
					ps.setString(1, kit);
					ps.setString(2, player.getUniqueId().toString());
					ps.execute();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(this.plugin);
	}

	public void mysql_registerPlayer(Boolean loadStats) {
		if (!this.plugin.getConfigManager().getMysqlEnabled()) {
			return;
		}
		StatsManager statsManager = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					ConfigManager configManager = plugin.getConfigManager();
					Connection connection = configManager.mysql_getConnection();

					Boolean status = configManager.mysql_checkConnection();
					if (!status) {
						plugin.getLogger().warning("Something is wrong with your MySQL server!");
						return;
					}

					// check if user already exists
					PreparedStatement ps = connection.prepareStatement("SELECT UUID FROM stats WHERE UUID = ?");
					ps.setString(1, player.getUniqueId().toString());
					ResultSet rs = ps.executeQuery();
					Boolean doesExists = rs.next();
					rs.close();
					ps.close();
					if (!doesExists) {
						String sql = "INSERT INTO stats(username, UUID) VALUES (?, ?)";
						PreparedStatement statement;
						statement = connection.prepareStatement(sql);
						statement.setString(1, player.getName());
						statement.setString(2, player.getUniqueId().toString());

						statement.execute();
						statement.close();

						String sql1 = "INSERT INTO selected_kit(username, UUID, selected) VALUES (?, ?, ?)";
						PreparedStatement statement1;
						statement1 = connection.prepareStatement(sql1);
						statement1.setString(1, player.getName());
						statement1.setString(2, player.getUniqueId().toString());
						statement1.setString(3, plugin.getKitsManager().getDefaultKit().getName());

						statement1.execute();
						statement1.close();
					}
					if (loadStats) {
						statsManager.loadStats();
						statsManager.loadSelectedKit();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(this.plugin);
	}

	public void plusOneStatInt(StatInt key) {
		this.statsInt.put(key, this.statsInt.get(key) + 1);
	}

	public void setStatInt(StatInt key, Integer value) {
		this.statsInt.put(key, value);
	}

	public Integer getStatInt(StatInt key) {
		return this.statsInt.get(key);
	}

	public void setStatDouble(StatDouble key, Double value) {
		this.statsDouble.put(key, value);
	}

	public Double getStatDouble(StatDouble key) {
		return this.statsDouble.get(key);
	}

	public void delete() {
		this.statsInt.clear();
	}

	public Player getPlayer() {
		return player;
	}

	public HashMap<StatInt, Integer> getStatsInt() {
		return this.statsInt;
	}

	public HashMap<StatInt, Integer> getStatsDouble() {
		return this.statsInt;
	}

	public PlayerInfo getpInfo() {
		return pInfo;
	}

	public Integer getEloChange() {
		return eloChange;
	}

	public void setEloChange(Double eloChange) {
		this.eloChange = eloChange.intValue();
	}

	private Integer getLeaderboardPlace() {
		ArrayList<String> allPlayers = new ArrayList<>();
		try {
			Connection connection = plugin.getConfigManager().getMysql_connection();
			PreparedStatement ps = connection.prepareStatement("SELECT UUID, elo FROM stats order by elo desc");
			ResultSet rs = ps.executeQuery();
			rs.next();

			while (rs.next()) {
				allPlayers.add(rs.getString(1));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		return allPlayers.indexOf(this.player.getUniqueId().toString()) + 1;
	}

	public Integer getLeaderboardPosition() {
		Integer out = leaderboardPositionCache;
		if (out == null) {
			out = this.getLeaderboardPlace();
			this.leaderboardPositionCache = out;
		}
		return out;
	}

}
