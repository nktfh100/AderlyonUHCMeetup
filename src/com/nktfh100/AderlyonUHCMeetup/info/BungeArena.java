package com.nktfh100.AderlyonUHCMeetup.info;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.utils.ServerStatus;

public class BungeArena {

	private String server;
	private String ip;
	private Integer port;
	private GameState gameState;
	private Integer maxPlayers;
	private Integer currentPlayers;

	private ServerStatus serverStatus;

	public BungeArena(String server, String ip, Integer port, GameState gameState, Integer maxPlayers, Integer currentPlayers) {
		this.server = server;
		this.ip = ip;
		this.port = port;
		this.gameState = gameState;
		this.maxPlayers = maxPlayers;
		this.currentPlayers = currentPlayers;
		this.serverStatus = new ServerStatus(ip, port);
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public String getServer() {
		return server;
	}

	public Integer getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(Integer maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public Integer getCurrentPlayers() {
		return currentPlayers;
	}

	public void setCurrentPlayers(Integer currentPlayers) {
		this.currentPlayers = currentPlayers;
	}

	public String getIp() {
		return ip;
	}

	public Integer getPort() {
		return port;
	}

	public ServerStatus getServerStatus() {
		return serverStatus;
	}

}
