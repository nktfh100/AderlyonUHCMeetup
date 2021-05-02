package com.nktfh100.AderlyonUHCMeetup.managers;

import java.util.ArrayList;

import com.nktfh100.AderlyonUHCMeetup.enums.StatInt;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class EloManager {
	private UHCMeetup plugin;

	public EloManager(UHCMeetup instance) {
		this.plugin = instance;
//		calcNewRatings();
	}

	public void calcNewRatings() {
		ArrayList<StatsManager> statsManagers = plugin.getPlayersManager().getAllStatsManagers();
		for (final StatsManager sp : statsManagers) {
			for (final StatsManager osp : statsManagers) {
				if (osp != sp) {
					sp.setEloChange(sp.getStatInt(StatInt.ELO) + sp.getEloChange()
							+ ratingChange(sp.getStatInt(StatInt.LAST_GAME_POSITION) == 0 ? plugin.getConfigManager().getDefaultElo() : sp.getStatInt(StatInt.ELO), osp.getStatInt(StatInt.ELO)));
				}
			}
		}
	}

	public double ratingChange(double rating1, double rating2) {
		double exponent = (double) (rating2 - rating1) / 400;
		double expected = (1 / (1 + (Math.pow(10, exponent))));
		double K = getK(rating1); // TODO K Value
		int score = 1;

		double newRating = (int) Math.round(rating1 + K * (score - expected));
		double ratingChange = newRating - rating1;

		if (score > .5 && ratingChange < 0) {
			ratingChange = 0;
		}
		return ratingChange;
	}

	public double getK(double rating1) {
		double K;
		if (rating1 < 2000) {
			K = 32;
		} else if (rating1 >= 2000 && rating1 < 2400) {
			K = 24;
		} else {
			K = 16;
		}
		return K;
	}
}