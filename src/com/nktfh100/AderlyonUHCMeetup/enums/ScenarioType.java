package com.nktfh100.AderlyonUHCMeetup.enums;

public enum ScenarioType {
	DEFAULT("default"), BOWLESS("bowless"), FIRELESS("fireless"), NOCLEAN("noClean"), HORSELESS("horseless"), RODLESS("rodless"), TIMEBOMB("timeBomb"), TNTONDEATH("tntOnDeath"),
	SWITCHEROO("switcheroo");

	private String name;

	private ScenarioType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
