package com.nktfh100.AderlyonUHCMeetup.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.nktfh100.AderlyonUHCMeetup.info.SoundInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.XSound;

public class SoundsManager {

	private UHCMeetup plugin;

	private HashMap<String, ArrayList<SoundInfo>> sounds = new HashMap<String, ArrayList<SoundInfo>>();

	public SoundsManager(UHCMeetup instance) {
		this.plugin = instance;
	}

	public void loadSounds() {
		File soundsConfigFIle = new File(this.plugin.getDataFolder(), "sounds.yml");
		if (!soundsConfigFIle.exists()) {
			try {
				this.plugin.saveResource("sounds.yml", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		YamlConfiguration soundsConfig = YamlConfiguration.loadConfiguration(soundsConfigFIle);
		try {
			this.sounds = new HashMap<String, ArrayList<SoundInfo>>();

			ConfigurationSection soundsSC = soundsConfig.getConfigurationSection("sounds");
			Set<String> soundsKeys = soundsSC.getKeys(false);
			for (String key : soundsKeys) {
				try {
					this.sounds.put(key, new ArrayList<SoundInfo>());
					if (soundsSC.getString(key).equalsIgnoreCase("none")) {
						continue;
					}
					for (String soundInfoStr : soundsSC.getString(key).split("-")) {
						String[] soundData = soundInfoStr.split(",");
						Sound soundType = XSound.matchXSound(soundData[0].toUpperCase()).get().parseSound();
						float volume;
						float volume2 = -1F;
						if (soundData[1].startsWith("@")) {
							String[] volumeData = soundData[1].replace("@", "").split("/");
							volume = Float.valueOf(volumeData[0]);
							volume2 = Float.valueOf(volumeData[1]);
						} else {
							volume = Float.parseFloat(soundData[1]);
						}

						float pitch;
						float pitch2 = -1F;
						if (soundData[2].startsWith("@")) {
							String[] pitchData = soundData[2].replace("@", "").split("/");
							pitch = Float.valueOf(pitchData[0]);
							pitch2 = Float.valueOf(pitchData[1]);
						} else {
							pitch = Float.parseFloat(soundData[2]);
						}

						int delay = Integer.parseInt(soundData[3]);
						int delay2 = -1;
						if (soundData[3].startsWith("@")) {
							String[] delayData = soundData[3].replace("@", "").split("/");
							delay = Integer.valueOf(delayData[0]);
							delay2 = Integer.valueOf(delayData[1]);
						} else {
							delay = Integer.parseInt(soundData[3]);
						}

						SoundInfo soundInfo = new SoundInfo(soundType, volume, pitch, delay, volume2, pitch2, delay2);

						this.sounds.get(key).add(soundInfo);
					}

				} catch (Exception e) {
					e.printStackTrace();
					this.plugin.getLogger().log(Level.SEVERE, "Something is wrong with your sounds.yml file! (" + key + ")");
					this.plugin.getPluginLoader().disablePlugin(this.plugin);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.plugin.getLogger().log(Level.SEVERE, "Something is wrong with your sounds.yml file!");
			this.plugin.getPluginLoader().disablePlugin(this.plugin);
		}
	}

	public void playSound(String key, Location loc) {
		for (SoundInfo soundInfo : this.getSound(key)) {
			if (soundInfo.getDelay() > 0) {
				new BukkitRunnable() {
					@Override
					public void run() {
						loc.getWorld().playSound(loc, soundInfo.getSound(), soundInfo.getVolume(), soundInfo.getPitch());
					}
				}.runTaskLater(this.plugin, soundInfo.getDelay());
			} else {
				loc.getWorld().playSound(loc, soundInfo.getSound(), soundInfo.getVolume(), soundInfo.getPitch());
			}
		}
	}

	public void playSound(String key, Player player) {
		for (SoundInfo soundInfo : this.getSound(key)) {
			if (soundInfo.getDelay() > 0) {
				new BukkitRunnable() {
					@Override
					public void run() {
						player.playSound(player.getLocation(), soundInfo.getSound(), soundInfo.getVolume(), soundInfo.getPitch());
					}
				}.runTaskLater(this.plugin, soundInfo.getDelay());
			} else {
				player.playSound(player.getLocation(), soundInfo.getSound(), soundInfo.getVolume(), soundInfo.getPitch());
			}
		}
	}

	public ArrayList<SoundInfo> getSound(String key) {
		ArrayList<SoundInfo> out = this.sounds.get(key);
		if (out == null) {
			this.plugin.getLogger().warning("Sound '" + key + "' is missing from your sounds.yml file!");
			return new ArrayList<SoundInfo>();
		}
		return out;
	}

}
