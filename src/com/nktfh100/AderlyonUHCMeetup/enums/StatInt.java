package com.nktfh100.AderlyonUHCMeetup.enums;

public enum StatInt {
	GAMES_PLAYED("games_played"), GAMES_WON("games_won"), KILLS("kills"), DEATHS("deaths"), TIME_PLAYED("time_played"), LAST_GAME_POSITION("last_game_position"),
	AVERAGE_POSITION_SUM("average_position_sum"), ELO("elo");

	private String name;

	private StatInt(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
