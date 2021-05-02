package com.nktfh100.AderlyonUHCMeetup.info;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.AderlyonUHCMeetup.utils.Utils;

public class ItemInfo {

	private Boolean isHead = false;
	private String texture;
	private Integer slot;
	private Material mat;
	private ItemStack item;
	private String title = "";
	private ArrayList<String> lore = new ArrayList<String>();

	public ItemInfo(Integer slot, Material mat, ItemStack item, String title, ArrayList<String> lore) {
		this.slot = slot;
		this.mat = mat;
		this.title = title;
		this.lore = lore;
		this.item = item;
		Utils.addItemFlag(this.item, ItemFlag.HIDE_ATTRIBUTES);
	}

	public void setHeadInfo(String texture) {
		this.isHead = true;
		this.texture = texture;
	}

	public ItemStack getItem(String... values) {
		if (this.isHead) {
			ItemStack out = this.item.clone();
			out = Utils.setItemName(out, this.getTitle(values), this.getLore(values));
			return out;
		} else {
			ItemStack out = this.item.clone();
			out = Utils.setItemName(out, this.getTitle(values), this.getLore(values));
			return out;
		}
	}

	private String replaceValues(String line, String... values) {
		if (line == null || line.isEmpty()) {
			return "";
		}
		int i = 0;
		for (String val : values) {
			if (i == 0) {
				line = line.replace("%value%", val);
			} else {
				line = line.replace("%value" + i + "%", val);

			}
		}

		return line;
	}

	// ------- title -------

	public String getTitle() {
		if (this.title == null || this.title.isEmpty()) {
			return "";
		}
		return title;
	}

	public String getTitle(String... values) {
		if (this.title == null || this.title.isEmpty()) {
			return "";
		}
		return replaceValues(this.title, values);
	}

	// ------- lore -------

	public ArrayList<String> getLore() {
		if (this.lore == null || this.lore.size() == 0) {
			return new ArrayList<String>();
		}
		return lore;
	}

	public ArrayList<String> getLore(String... values) {
		if (this.lore == null || this.lore.size() == 0) {
			return new ArrayList<String>();
		}
		ArrayList<String> newLore = new ArrayList<String>();
		for (String line : this.lore) {
			newLore.add(this.replaceValues(line, values));
		}
		return newLore;
	}

	public Integer getSlot() {
		return slot;
	}

	public Material getMat() {
		return mat;
	}

	public Boolean getIsHead() {
		return isHead;
	}

	public String getTexture() {
		return texture;
	}
}
