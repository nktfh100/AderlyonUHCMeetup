package com.nktfh100.AderlyonUHCMeetup.utils.compatibility.versions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nktfh100.AderlyonUHCMeetup.enums.BiomeData;
import com.nktfh100.AderlyonUHCMeetup.utils.compatibility.PluginVersion;

import net.minecraft.server.v1_16_R3.BiomeBase;
import net.minecraft.server.v1_16_R3.BiomeRegistry;
import net.minecraft.server.v1_16_R3.Biomes;
import net.minecraft.server.v1_16_R3.RegistryGeneration;
import net.minecraft.server.v1_16_R3.ResourceKey;

public class V1_16_R3 implements PluginVersion {

	Map<String, BiomeBase> biomeBackup = new HashMap<>();

	public void biomeSwapper() {
		List<BiomeBase> list = (List<BiomeBase>) RegistryGeneration.WORLDGEN_BIOME.g().collect(Collectors.toList());
		for (BiomeBase biomeBase : list) {
			this.biomeBackup.put(RegistryGeneration.WORLDGEN_BIOME.getKey(biomeBase).getKey().toLowerCase(), biomeBase);
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

	public static Object test(Field paramField, Object paramObject) throws Exception {
		try {
			paramField.setAccessible(true);

			return paramField.get(paramObject);
		} catch (ReflectiveOperationException reflectiveOperationException) {
			throw new Exception("Could not get field " + paramField.getName() + " in instance " + ((paramObject != null) ? paramObject : paramField).getClass().getSimpleName());
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T test1(Class<?> paramClass, String paramString, Object paramObject) throws Exception {
		String str = paramClass.getSimpleName();

		do {
			for (Field field : paramClass.getDeclaredFields()) {
				if (field.getName().equals(paramString))
					return (T) test(field, paramObject);
			}
		} while (!(paramClass = paramClass.getSuperclass()).isAssignableFrom(Object.class));

		throw new Exception("No such field " + paramString + " in " + str + " or its superclasses");
	}

	public static <T> T test2(Class<?> paramClass, String paramString) throws Exception {
		if (paramClass == null)
			throw new NullPointerException("clazz is marked non-null but is null");
		return test1(paramClass, paramString, null);
	}

	public void swap(BiomeData paramBiomeData1, BiomeData paramBiomeData2) throws Exception {
		Method method = BiomeRegistry.class.getDeclaredMethod("a", new Class[] { int.class, ResourceKey.class, BiomeBase.class });
		method.setAccessible(true);
		method.invoke(null,
				new Object[] { Integer.valueOf(paramBiomeData1.getId()), test2(Biomes.class, paramBiomeData2.getKey_1_13()), this.biomeBackup.get(paramBiomeData2.getKey_1_13().toLowerCase()) });
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
