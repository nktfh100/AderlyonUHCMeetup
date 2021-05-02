package com.nktfh100.AderlyonUHCMeetup.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.nktfh100.AderlyonUHCMeetup.info.KitInfo;
import com.nktfh100.AderlyonUHCMeetup.info.KitItemInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.SkullUtils;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;
import com.nktfh100.AderlyonUHCMeetup.utils.XMaterial;

public class KitsManager {

	private UHCMeetup plugin;

	private File kitsFolder;
	private HashMap<String, KitInfo> kits = new HashMap<String, KitInfo>();

	public KitsManager(UHCMeetup instance) {
		this.plugin = instance;
		this.kitsFolder = new File(this.plugin.getDataFolder(), "kits");
		if (!kitsFolder.exists()) {
			try {
				this.kitsFolder.mkdir();
				this.plugin.getVersionHandler().copyInputStreamToFile(this.plugin.getResource("defaultKit.yml"), new File(this.plugin.getDataFolder(), "kits" + File.separator + "defaultKit.yml"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loadKits() {
		this.kits = new HashMap<String, KitInfo>();
		// load all kits
		File[] listOfArenaFiles = this.kitsFolder.listFiles();
		for (File file : listOfArenaFiles) {
			YamlConfiguration kitFile = YamlConfiguration.loadConfiguration(file);
			try {
				String kitName = kitFile.getString("name", "kit-name");
				Boolean enabled = kitFile.getBoolean("enabled", true);
				KitInfo kitInfo = new KitInfo(kitName, enabled);

				if (kitFile.getConfigurationSection("displayItem") != null) {
					ConfigurationSection displayItemSC = kitFile.getConfigurationSection("displayItem");
					KitItemInfo kitItemInfo = this.loadItem(displayItemSC, "displayItem", kitName);
					kitInfo.setDisplayItem(kitItemInfo.getItem());
				}

				// Load items
				ConfigurationSection itemsSC = kitFile.getConfigurationSection("items");
				if (itemsSC != null) {
					for (String itemKey : itemsSC.getKeys(false)) {
						ConfigurationSection itemSC = itemsSC.getConfigurationSection(itemKey);
						KitItemInfo kitItemInfo = this.loadItem(itemSC, itemKey, kitName);
						if (kitItemInfo == null) {
							continue;
						}
						kitInfo.addItem(kitItemInfo);
					}
				}

				// load Armor
				ConfigurationSection armorSC = kitFile.getConfigurationSection("armor");
				if (armorSC != null) {
					for (String armorItemKey : armorSC.getKeys(false)) {
						ConfigurationSection armorItemSC = armorSC.getConfigurationSection(armorItemKey);

						KitItemInfo kitItemInfo = this.loadItem(armorItemSC, armorItemKey, kitName);
						if (kitItemInfo == null) {
							continue;
						}

						kitInfo.addArmor(armorItemKey, kitItemInfo);
					}
				}

				this.kits.put(kitInfo.getName(), kitInfo);
			} catch (Exception e) {
				e.printStackTrace();
				this.plugin.getLogger().info("ERROR! Something is wrong with your " + file.getName() + " file!");
			}
		}
	}

	public KitItemInfo loadItem(ConfigurationSection section, String itemKey, String kitName) {
		Integer slot = section.getInt("slot", 0);
		String matStr = section.getString("material", "AIR");
		ItemStack item;
		if (matStr.startsWith("@")) {
			item = XMaterial.PLAYER_HEAD.parseItem();
			SkullMeta headMeta = (SkullMeta) item.getItemMeta();
			headMeta = SkullUtils.applySkin(headMeta, matStr.replaceAll("@", ""));
			item.setItemMeta(headMeta);
		} else {
			Material mat = XMaterial.valueOf(matStr).parseMaterial();
			if (mat == Material.AIR) {
				return null;
			}
			item = XMaterial.valueOf(matStr).parseItem();
		}

		Utils.setItemName(item, section.getString("name"), (ArrayList<String>) section.getStringList("lore"));
		item.setAmount(section.getInt("amount", 1));

		ConfigurationSection itemEnchSC = section.getConfigurationSection("enchantments");
		if (itemEnchSC != null) {
			for (String enchKey : itemEnchSC.getKeys(false)) {
				@SuppressWarnings("deprecation")
				Enchantment ench = Enchantment.getByName(enchKey);
				if (ench != null) {
					Utils.enchantedItem(item, ench, itemEnchSC.getInt(enchKey, 1));
				} else {
					this.plugin.getLogger().info("Enchantment " + enchKey + " is invalid for item " + itemKey + " for kit " + kitName);
				}
			}
		}
		return new KitItemInfo(slot, item);
	}

	public KitInfo getDefaultKit() {
		if (this.kits.size() > 0) {
			KitInfo out = this.getKit(this.plugin.getConfigManager().getDefaultKit());
			if (out != null) {
				return out;
			}
			return this.kits.values().iterator().next();
		}
		return null;
	}

	public KitInfo getKit(String key) {
		return this.kits.get(key);
	}

	public Collection<KitInfo> getKits() {
		return this.kits.values();
	}

}
