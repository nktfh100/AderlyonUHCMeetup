package com.nktfh100.AderlyonUHCMeetup.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.info.BungeArena;
import com.nktfh100.AderlyonUHCMeetup.info.ItemInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.managers.BungeManager;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;

public class BungeArenaSelectorInv extends CustomHolder {

	public BungeArenaSelectorInv() {
		super(45, UHCMeetup.getInstance().getMessagesManager().getGameMsg("arenaSelectorInvTitle"));
	}

	public void update() {
		UHCMeetup plugin = UHCMeetup.getInstance();
		this.clearInv();
		Material mat = plugin.getItemsManager().getItem("arenaSelector_border").getItem().getMat();
		Utils.addBorder(this.inv, 45, mat);

		BungeManager bungeManager = plugin.getBungeManager();

		final Boolean showRunning = plugin.getConfigManager().getShowRunningArenas();
		for (BungeArena arena : bungeManager.getAllArenas()) {
			Boolean canJoin = true;
			if (arena.getGameState() == GameState.RUNNING || arena.getGameState() == GameState.LOADING) {
				canJoin = false;
			}
			if (arena.getCurrentPlayers() == arena.getMaxPlayers()) {
				canJoin = false;
			}
			if (!showRunning && !canJoin) {
				continue;
			}
			String gameStateStr = arena.getGameState().toString();
			ItemInfo arenaItem = canJoin ? plugin.getItemsManager().getItem("arenaSelector_arena").getItem2() : plugin.getItemsManager().getItem("arenaSelector_arena").getItem();
			ItemStack arenaItemS = arenaItem.getItem(arena.getServer(), arena.getCurrentPlayers() + "", arena.getMaxPlayers() + "", "" + Utils.getStateColor(arena.getGameState()), gameStateStr);
			Icon icon = new Icon(arenaItemS);

			if (canJoin) {
				icon.addClickAction(new ClickAction() {
					@Override
					public void execute(Player player) {
						plugin.sendPlayerToArena(player, arena.getServer());
					}
				});
			}
			this.addIcon(icon);
		}
	}
}