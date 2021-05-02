package com.nktfh100.AderlyonUHCMeetup.utils.compatibility.versions;

import net.minecraft.server.v1_8_R3.BiomeBase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.bukkit.inventory.ItemStack;
import org.apache.commons.io.FileUtils;

import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.compatibility.PluginVersion;

public class V1_8_R3 implements PluginVersion {

	public void biomeSwapper() {
		Field biomesField = null;
		try {
			biomesField = BiomeBase.class.getDeclaredField("biomes");
			biomesField.setAccessible(true);
			if ((biomesField.get(null) instanceof BiomeBase[])) {
				BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
				biomes[BiomeBase.OCEAN.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.EXTREME_HILLS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.FOREST.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.TAIGA.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.SWAMPLAND.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.RIVER.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.HELL.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.SKY.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.FROZEN_OCEAN.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.FROZEN_RIVER.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.ICE_PLAINS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.ICE_MOUNTAINS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.MUSHROOM_ISLAND.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.MUSHROOM_SHORE.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.BEACH.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.DESERT_HILLS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.FOREST_HILLS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.TAIGA_HILLS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.SMALL_MOUNTAINS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.JUNGLE.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.JUNGLE_HILLS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.JUNGLE_EDGE.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.STONE_BEACH.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.COLD_BEACH.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.BIRCH_FOREST.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.BIRCH_FOREST_HILLS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.ROOFED_FOREST.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.COLD_TAIGA.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.COLD_TAIGA_HILLS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.MEGA_TAIGA.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.MEGA_TAIGA_HILLS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.EXTREME_HILLS_PLUS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.SAVANNA.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.SAVANNA_PLATEAU.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.MESA.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.MESA_PLATEAU_F.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.MESA_PLATEAU.id] = BiomeBase.PLAINS;
				biomesField.set(null, biomes);
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException localException) {
		}
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
