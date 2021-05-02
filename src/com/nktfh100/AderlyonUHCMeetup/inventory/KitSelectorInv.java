package com.nktfh100.AderlyonUHCMeetup.inventory;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.AderlyonUHCMeetup.info.KitInfo;
import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;

public class KitSelectorInv extends CustomHolder {

	private PlayerInfo pInfo;

	public KitSelectorInv(PlayerInfo pInfo) {
		super(18, UHCMeetup.getInstance().getMessagesManager().getGameMsg("kitSelectorTitle"));
		this.pInfo = pInfo;

		KitSelectorInv inv = this;

		String playerKit = pInfo.getKit();
		if (playerKit == null) {
			playerKit = UHCMeetup.getInstance().getKitsManager().getDefaultKit().getName();
		}
		for (KitInfo kitInfo : UHCMeetup.getInstance().getKitsManager().getKits()) {
			ItemStack item = kitInfo.getDisplayItem().clone();
			if (kitInfo.getName().equals(playerKit)) {
				item = Utils.enchantedItem(item, Enchantment.DURABILITY, 1);
				item = Utils.addItemFlag(item, ItemFlag.HIDE_ENCHANTS);
			}
			Icon icon = new Icon(item);
			icon.addClickAction(new ClickAction() {
				@Override
				public void execute(Player player) {
					inv.kitClick(kitInfo);
				}
			});
			this.addIcon(icon);
		}
	}

	public void kitClick(KitInfo kit) {
		this.pInfo.setKit(kit.getName());
		this.pInfo.getPlayer().sendMessage(UHCMeetup.getInstance().getMessagesManager().getGameMsg("selectedKit", kit.getName()));
		UHCMeetup.getInstance().getSoundsManager().playSound("playerSelectKit", this.pInfo.getPlayer());
		this.pInfo.getStatsManager().saveSelectedKit(kit.getName());
		this.pInfo.getPlayer().closeInventory();
	}

}