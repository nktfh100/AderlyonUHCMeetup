package com.nktfh100.AderlyonUHCMeetup.info;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.AderlyonUHCMeetup.utils.Utils;

public class KitItemInfo {

	private Integer slot;
	private ItemStack item;

	public KitItemInfo(Integer slot, ItemStack item) {
		this.slot = slot;
		this.item = item;

		Utils.addItemFlag(this.item, ItemFlag.HIDE_ATTRIBUTES);
	}

	// For armor
	public KitItemInfo(ItemStack item) {
		this.item = item;
		this.slot = 0;

		Utils.addItemFlag(this.item, ItemFlag.HIDE_ATTRIBUTES);
	}

	public ItemStack getItemClone() {
		return this.item.clone();
	}

	public ItemStack getItem() {
		return this.item;
	}

	public Integer getSlot() {
		return this.slot;
	}
}
