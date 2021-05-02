package com.nktfh100.AderlyonUHCMeetup.enums;

public enum StatDouble {
	AVERAGE_POSITION("average_position"), KDR("kdr");
	private String name;

	private StatDouble(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
