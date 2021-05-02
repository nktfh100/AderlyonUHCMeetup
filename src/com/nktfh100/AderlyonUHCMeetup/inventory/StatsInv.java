package com.nktfh100.AderlyonUHCMeetup.inventory;

import com.nktfh100.AderlyonUHCMeetup.enums.StatDouble;
import com.nktfh100.AderlyonUHCMeetup.enums.StatInt;
import com.nktfh100.AderlyonUHCMeetup.info.ItemInfo;
import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;

public class StatsInv extends CustomHolder {

	private PlayerInfo pInfo;

	public StatsInv(PlayerInfo pInfo) {
		super(54, UHCMeetup.getInstance().getMessagesManager().getGameMsg("playerStatsInvTitle", pInfo.getPlayer().getName()));
		this.pInfo = pInfo;
		this.update();
	}

	public void update() {
		UHCMeetup plugin = UHCMeetup.getInstance();
		this.inv.clear();
		Utils.fillInv(this.inv, plugin.getItemsManager().getItem("stats_background").getItem().getItem());

		ItemInfo statsItemInfo_ = plugin.getItemsManager().getItem("stats_wins").getItem();
		this.inv.setItem(statsItemInfo_.getSlot(), statsItemInfo_.getItem(pInfo.getStatsManager().getStatInt(StatInt.GAMES_WON) + ""));

		statsItemInfo_ = plugin.getItemsManager().getItem("stats_elo").getItem();
		this.inv.setItem(statsItemInfo_.getSlot(), statsItemInfo_.getItem(pInfo.getStatsManager().getStatInt(StatInt.ELO) + ""));

		statsItemInfo_ = plugin.getItemsManager().getItem("stats_leaderboardPosition").getItem();
		this.inv.setItem(statsItemInfo_.getSlot(), statsItemInfo_.getItem(pInfo.getStatsManager().getLeaderboardPosition() + ""));

		statsItemInfo_ = plugin.getItemsManager().getItem("stats_winProbability").getItem();
		Double winProbability = 0.0;
		if (pInfo.getStatsManager().getStatInt(StatInt.GAMES_PLAYED) > 0) {
			winProbability = Math.round(((double) ((pInfo.getStatsManager().getStatInt(StatInt.GAMES_WON) * 100) / pInfo.getStatsManager().getStatInt(StatInt.GAMES_PLAYED))) * 100) / 100.0;
			if (winProbability.isNaN() || winProbability.isInfinite()) {
				winProbability = 0.0;
			}
		}
		this.inv.setItem(statsItemInfo_.getSlot(), statsItemInfo_.getItem(winProbability + ""));

		statsItemInfo_ = plugin.getItemsManager().getItem("stats_averagePosition").getItem();
		Double averagePosition = pInfo.getStatsManager().getStatDouble(StatDouble.AVERAGE_POSITION);
		if (averagePosition == 0 && pInfo.getStatsManager().getStatInt(StatInt.GAMES_PLAYED) > 0) {
			averagePosition = (double) pInfo.getStatsManager().getStatInt(StatInt.AVERAGE_POSITION_SUM) / (double) pInfo.getStatsManager().getStatInt(StatInt.GAMES_PLAYED);
			if (averagePosition.isNaN() || averagePosition.isInfinite()) {
				averagePosition = 0.0;
			}
		}
		averagePosition = Math.round(averagePosition * 100) / 100.0;

		this.inv.setItem(statsItemInfo_.getSlot(), statsItemInfo_.getItem(averagePosition + ""));

		statsItemInfo_ = plugin.getItemsManager().getItem("stats_gamesPlayed").getItem();
		this.inv.setItem(statsItemInfo_.getSlot(), statsItemInfo_.getItem(pInfo.getStatsManager().getStatInt(StatInt.GAMES_PLAYED) + ""));
	}

}
