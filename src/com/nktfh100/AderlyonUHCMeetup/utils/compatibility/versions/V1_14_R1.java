package com.nktfh100.AderlyonUHCMeetup.utils.compatibility.versions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import com.nktfh100.AderlyonUHCMeetup.enums.BiomeData;
import com.nktfh100.AderlyonUHCMeetup.utils.compatibility.PluginVersion;

import net.minecraft.server.v1_14_R1.BiomeBase;
import net.minecraft.server.v1_14_R1.IRegistry;

public class V1_14_R1 implements PluginVersion {

	Map<String, BiomeBase> biomeBackup = new HashMap<>();

	public void biomeSwapper() {
		List<BiomeBase> base = (List<BiomeBase>) IRegistry.BIOME.d().collect(Collectors.toList());
		for (BiomeBase b : base) {
			this.biomeBackup.put(IRegistry.BIOME.getKey(b).getKey(), b);
		}

		try {
			this.swap(BiomeData.OCEAN, BiomeData.PLAINS);
			this.swap(BiomeData.EXTREME_HILLS, BiomeData.PLAINS);
			this.swap(BiomeData.FOREST, BiomeData.PLAINS);
			this.swap(BiomeData.TAIGA, BiomeData.PLAINS);
			this.swap(BiomeData.SWAMPLAND, BiomeData.PLAINS);
			this.swap(BiomeData.RIVER, BiomeData.PLAINS);
			this.swap(BiomeData.HELL, BiomeData.PLAINS);
			this.swap(BiomeData.SKY, BiomeData.PLAINS);
			this.swap(BiomeData.FROZEN_OCEAN, BiomeData.PLAINS);
			this.swap(BiomeData.FROZEN_RIVER, BiomeData.PLAINS);
			this.swap(BiomeData.ICE_MOUNTAINS, BiomeData.PLAINS);
			this.swap(BiomeData.BEACHES, BiomeData.PLAINS);
			this.swap(BiomeData.DESERT_HILLS, BiomeData.PLAINS);
			this.swap(BiomeData.FOREST_HILLS, BiomeData.PLAINS);
			this.swap(BiomeData.TAIGA_HILLS, BiomeData.PLAINS);
			this.swap(BiomeData.JUNGLE, BiomeData.PLAINS);
			this.swap(BiomeData.JUNGLE_HILLS, BiomeData.PLAINS);
			this.swap(BiomeData.JUNGLE_EDGE, BiomeData.PLAINS);
			this.swap(BiomeData.DEEP_OCEAN, BiomeData.PLAINS);
			this.swap(BiomeData.STONE_BEACH, BiomeData.PLAINS);
			this.swap(BiomeData.COLD_BEACH, BiomeData.PLAINS);
			this.swap(BiomeData.BIRCH_FOREST, BiomeData.PLAINS);
			this.swap(BiomeData.BIRCH_FOREST_HILLS, BiomeData.PLAINS);
			this.swap(BiomeData.ROOFED_FOREST, BiomeData.PLAINS);
			this.swap(BiomeData.TAIGA_COLD, BiomeData.PLAINS);
			this.swap(BiomeData.TAIGA_COLD_HILLS, BiomeData.PLAINS);
			this.swap(BiomeData.MEGA_TAIGA, BiomeData.PLAINS);
			this.swap(BiomeData.MEGA_TAIGA_HILLS, BiomeData.PLAINS);
			this.swap(BiomeData.EXTREME_HILLS_PLUS, BiomeData.PLAINS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void swap(BiomeData from, BiomeData to) throws Exception {
		IRegistry.a(IRegistry.BIOME, from.getId(), to.getKey_1_13().toLowerCase(), this.biomeBackup.get(to.getKey_1_13().toLowerCase()));
	}
	
	@Override
	public void copyInputStreamToFile(InputStream inputStream, File file) {
		try {
			FileUtils.copyInputStreamToFile(inputStream, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
