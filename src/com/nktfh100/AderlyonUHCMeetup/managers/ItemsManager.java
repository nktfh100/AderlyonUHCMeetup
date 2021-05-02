package com.nktfh100.AderlyonUHCMeetup.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.AderlyonUHCMeetup.info.ItemInfo;
import com.nktfh100.AderlyonUHCMeetup.info.ItemInfoContainer;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;
import com.nktfh100.AderlyonUHCMeetup.utils.XMaterial;

public class ItemsManager {

	private UHCMeetup plugin;

	private final static ArrayList<String> nums = new ArrayList<String>(Arrays.asList("", "2", "3"));
	private HashMap<String, ItemInfoContainer> items = new HashMap<String, ItemInfoContainer>();

	public ItemsManager(UHCMeetup instance) {
		this.plugin = instance;
	}

	public void loadItems() {
		File itemsConfigFIle = new File(this.plugin.getDataFolder(), "items.yml");
		if (!itemsConfigFIle.exists()) {
			try {
				this.plugin.saveResource("items.yml", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		YamlConfiguration itemsConfig = YamlConfiguration.loadConfiguration(itemsConfigFIle);
		try {
			this.items = new HashMap<String, ItemInfoContainer>();

			ConfigurationSection itemsSC = itemsConfig.getConfigurationSection("items");
			Set<String> itemsKeys = itemsSC.getKeys(false);
			for (String key : itemsKeys) {
				try {
					ConfigurationSection itemSC = itemsSC.getConfigurationSection(key);

					ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();

					for (String num : nums) {
						Integer slot = itemSC.getInt("slot", 0);
						Boolean isHead = false;
						String matStr = itemSC.getString("mat" + num, "BARRIER");
						Material mat_;
						ItemStack item_ = null;
						if (matStr.startsWith("@") && matStr.endsWith("@")) {
							isHead = true;
							matStr = matStr.replaceAll("@", "");
							mat_ = XMaterial.PLAYER_HEAD.parseMaterial();
							item_ = new ItemStack(mat_);
							item_ = Utils.applyHeadTexture(item_, matStr);
						} else {
							Optional<XMaterial> matMatched_ = XMaterial.matchXMaterial(matStr);
							mat_ = matMatched_.get().parseMaterial();
							item_ = matMatched_.get().parseItem();
						}
						if (mat_ == null) {
							mat_ = Material.BARRIER;
							item_ = new ItemStack(mat_);
						}

						String title = ChatColor.translateAlternateColorCodes('&', itemSC.getString("title" + num, " "));
						ArrayList<String> lore = new ArrayList<String>(itemSC.getStringList("lore" + num));
						for (int ii = 0; ii < lore.size(); ii++) {
							lore.set(ii, ChatColor.translateAlternateColorCodes('&', lore.get(ii)));
						}
						ItemInfo itemInfo = new ItemInfo(slot, mat_, item_, title, lore);
						if (isHead) {
							itemInfo.setHeadInfo(matStr);
						}
						items.add(itemInfo);
					}

					this.items.put(key, new ItemInfoContainer(items.get(0), items.get(1), items.get(2)));
				} catch (Exception e) {
					e.printStackTrace();
					this.plugin.getLogger().log(Level.SEVERE, "Something is wrong with your items.yml file! (" + key + ")");
					this.plugin.getPluginLoader().disablePlugin(this.plugin);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.plugin.getLogger().log(Level.SEVERE, "Something is wrong with your items.yml file!");
			this.plugin.getPluginLoader().disablePlugin(this.plugin);
		}
	}

	public ItemInfoContainer getItem(String key) {
		ItemInfoContainer out = this.items.get(key);
		if (out == null) {
			this.plugin.getLogger().warning("Item '" + key + "' is missing from your items.yml file!");
			ItemInfo itemInfo = new ItemInfo(0, Material.BARRIER, new ItemStack(Material.BARRIER), "ITEM MISSING", new ArrayList<String>());
			return new ItemInfoContainer(itemInfo, itemInfo, itemInfo);
		}
		return out;
	}

}
