package com.nktfh100.AderlyonUHCMeetup.info;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.AderlyonUHCMeetup.enums.ScenarioType;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.XMaterial;

public class KitInfo {

	private String name;
	private Boolean enabled;
	private ItemStack displayItem = null;

	private ArrayList<KitItemInfo> items = new ArrayList<KitItemInfo>();

	private HashMap<String, KitItemInfo> armor = new HashMap<String, KitItemInfo>();

	public KitInfo(String name, Boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}

	public void giveKit(Player player) {
		ScenarioType st = UHCMeetup.getInstance().getGameManager().getActiveScenario();
		for (KitItemInfo itemInfo : this.items) {
			ItemStack item_ = itemInfo.getItemClone();
			if (st == ScenarioType.BOWLESS && item_.getType() == XMaterial.BOW.parseMaterial()) {
				continue;
			} else if (st == ScenarioType.RODLESS && item_.getType() == XMaterial.FISHING_ROD.parseMaterial()) {
				continue;
			} else if (st == ScenarioType.FIRELESS) {
				item_.removeEnchantment(Enchantment.ARROW_FIRE);
				item_.removeEnchantment(Enchantment.FIRE_ASPECT);
			} else if (st == ScenarioType.HORSELESS && item_.getType() == XMaterial.HORSE_SPAWN_EGG.parseMaterial()) {
				continue;
			}
			player.getInventory().setItem(itemInfo.getSlot(), item_);
		}
		KitItemInfo helmet = this.armor.get("helmet");
		if (helmet != null && helmet.getItem().getType() != Material.AIR) {
			player.getInventory().setHelmet(helmet.getItemClone());
		}
		KitItemInfo chestplate = this.armor.get("chestplate");
		if (chestplate != null && chestplate.getItem().getType() != Material.AIR) {
			player.getInventory().setChestplate(chestplate.getItemClone());
		}
		KitItemInfo leggings = this.armor.get("leggings");
		if (leggings != null && leggings.getItem().getType() != Material.AIR) {
			player.getInventory().setLeggings(leggings.getItemClone());
		}
		KitItemInfo boots = this.armor.get("boots");
		if (boots != null && boots.getItem().getType() != Material.AIR) {
			player.getInventory().setBoots(boots.getItemClone());
		}
	}

	public void addArmor(String key, KitItemInfo item) {
		this.armor.put(key, item);
	}

	public void addItem(KitItemInfo item) {
		this.items.add(item);
	}

	public ArrayList<KitItemInfo> getItems() {
		return this.items;
	}

	public String getName() {
		return name;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public ItemStack getDisplayItem() {
		if (this.displayItem == null) {
			return new ItemStack(Material.BARRIER);
		}
		return displayItem;
	}

	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
	}

}
