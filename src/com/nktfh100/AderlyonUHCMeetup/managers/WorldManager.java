package com.nktfh100.AderlyonUHCMeetup.managers;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.LoadChunksTask;
import com.nktfh100.AderlyonUHCMeetup.utils.XMaterial;

public class WorldManager {

	private UHCMeetup plugin;

	private Boolean isGenerating = false;;
	private Boolean isFinished = false;
	private World world;

	public WorldManager(UHCMeetup instance) {
		this.plugin = instance;
	}

	@SuppressWarnings("deprecation")
	public void generateNewWorld() {
		this.isGenerating = true;
		this.isFinished = false;
		if (this.plugin.getIsLobby()) {
			return;
		}
		if (new File("uhc").exists()) {
			if (Bukkit.getWorld("uhc") != null) {
				Bukkit.unloadWorld("uhc", false);
			}
			this.deleteDirectory(new File("uhc"));
			new BukkitRunnable() {
				@Override
				public void run() {
					generateNewWorld();
				}
			}.runTaskLater(this.plugin, 20L);
			return;
		}
		this.isGenerating = true;
		WorldCreator worldCreator = new WorldCreator("uhc");
		worldCreator.generateStructures(false);
		World world = null;
		try {
			world = this.plugin.getServer().createWorld(worldCreator);
			this.setWorld(world);
		} catch (Exception e) {
			this.plugin.getLogger().info("World NPE when trying to generate map.");
			Bukkit.getServer().unloadWorld(world, false);
			this.deleteDirectory(new File("uhc"));
			new BukkitRunnable() {
				@Override
				public void run() {
					generateNewWorld();
				}
			}.runTaskLater(this.plugin, 20L);
			return;
		}

		int waterCount = 0;

		this.plugin.getLogger().info("Loaded a new world.");

		Integer borderSize = this.plugin.getConfigManager().getBorderStages().get(0).getSize();
		boolean flag = false;
		for (int i = -borderSize; i <= borderSize; ++i) {
			boolean isInvalid = false;
			for (int j = -borderSize; j <= borderSize; j++) {
				boolean isCenter = i >= -100 && i <= 100 && j >= -100 && j <= 100;
				if (isCenter) {
					Block block = world.getHighestBlockAt(i, j).getLocation().add(0, -1, 0).getBlock();
					if (block.getType() == XMaterial.WATER.parseMaterial() || block.getType() == XMaterial.LAVA.parseMaterial()) {
						++waterCount;
					}
				}

				if (waterCount >= 2500) {
					this.plugin.getLogger().info("Invalid center, too much water/lava.");
					isInvalid = true;
					break;
				}
			}

			if (isInvalid) {
				flag = true;
				break;
			}
		}

		if (flag) {
			this.plugin.getLogger().info("Failed to find a good seed (" + world.getSeed() + ").");
			Bukkit.getServer().unloadWorld(world, false);

			this.deleteDirectory(new File("uhc"));

			this.isGenerating = false;
			new BukkitRunnable() {
				@Override
				public void run() {
					generateNewWorld();
				}
			}.runTaskLater(this.plugin, 35L);
			return;
		} else {
			this.plugin.getLogger().info("Found a good seed (" + world.getSeed() + ").");
		}

		world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0) + 3, 0);
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("announceAdvancements", "false");
		world.setGameRuleValue("showDeathMessages", "false");
		world.setGameRuleValue("doImmediateRespawn", "true");
		world.setGameRuleValue("spectatorsGenerateChunks", "false");
		world.setGameRuleValue("doMobSpawning", "false");

		world.setTime(6000);
		this.setIsGenerating(false);
		this.setIsFinished(true);
		this.plugin.getLogger().info("Generated uhc world succesfully.");
		world.save();
		this.loadChunks();
		this.plugin.getGameManager().createBorder(this.plugin.getConfigManager().getBorderStages().get(0).getSize());
		// Create Lock file
		try {
			File lock = new File("uhc" + File.separator + "gen.lock");
			lock.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
//							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}
		this.plugin.getGameManager().setGameState(GameState.WAITING);
	}

	public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public void loadChunks() {
		if (this.world == null || !this.isFinished) {
			return;
		}
		plugin.getLogger().info("Starting to load the chunks..");
		LoadChunksTask task_ = new LoadChunksTask(this.plugin.getServer(), "uhc", 30, 5000 / 20, 20, false);
		this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, task_, 2, 2);
	}

	public Boolean getIsGenerating() {
		return isGenerating;
	}

	public Boolean getIsFinished() {
		return isFinished;
	}

	public void setIsFinished(Boolean is) {
		this.isFinished = is;
	}

	public void setIsGenerating(Boolean is) {
		this.isGenerating = is;
	}

	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
