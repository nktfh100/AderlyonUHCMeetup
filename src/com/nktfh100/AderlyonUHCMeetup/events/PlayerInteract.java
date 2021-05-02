package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.info.PlayerInfo;
import com.nktfh100.AderlyonUHCMeetup.inventory.KitSelectorInv;
import com.nktfh100.AderlyonUHCMeetup.inventory.StatsInv;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.managers.ItemsManager;

public class PlayerInteract implements Listener {

	@EventHandler
	public void onPlayerUse(PlayerInteractEvent ev) {
		UHCMeetup plugin = UHCMeetup.getInstance();
		if (!plugin.getIsLobby() && plugin.getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
		}
		Player player = ev.getPlayer();
		PlayerInfo pInfo = plugin.getPlayersManager().getPlayerInfo(player);
		if (!plugin.getIsLobby() && pInfo.isSpectating()) {
			ev.setCancelled(true);
		}
		if (ev.getItem() == null) {
			return;
		}
		String displayName = ev.getItem().getItemMeta().getDisplayName();
		if (displayName != null && (ev.getAction() == Action.RIGHT_CLICK_AIR || ev.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			ItemsManager itemsManager = plugin.getItemsManager();
			if (plugin.getIsLobby()) {
				if (displayName.equals(itemsManager.getItem("arenaSelector").getItem().getTitle())) {
					plugin.getBungeManager().openArenaSelector(pInfo);
					ev.setCancelled(true);
					return;
				}
				return;
			}
			if (plugin.getGameManager().getGameState() != GameState.RUNNING) {
				if (displayName.equals(itemsManager.getItem("leave").getItem().getTitle())) {
					ev.setCancelled(true);
					plugin.sendPlayerToLobby(player);
					return;
				} else if (displayName.equals(itemsManager.getItem("kitSelector").getItem().getTitle())) {
					ev.setCancelled(true);
					player.openInventory(new KitSelectorInv(plugin.getPlayersManager().getPlayerInfo(player)).getInventory());
					return;
				} else if (displayName.equals(itemsManager.getItem("scenarioVote").getItem().getTitle())) {
					ev.setCancelled(true);
					plugin.getGameManager().getScenarioVoteInv().openInv(player);
					return;
				} else if (displayName.equals(itemsManager.getItem("stats").getItem().getTitle())) {
					ev.setCancelled(true);
					player.openInventory(new StatsInv(plugin.getPlayersManager().getPlayerInfo(player)).getInventory());
					return;
				}
			} else {
				if (displayName.equals(itemsManager.getItem("goldenHead").getItem().getTitle())) {
					ev.setCancelled(true);
					for (PotionEffect pe : plugin.getConfigManager().getGoldenHeadEffects()) {
						player.removePotionEffect(pe.getType());
						player.addPotionEffect(pe);
					}
					if (ev.getItem().getAmount() == 1) {
						player.getInventory().remove(ev.getItem());
					} else {
						ev.getItem().setAmount(ev.getItem().getAmount() - 1);
					}
					plugin.getSoundsManager().playSound("goldenHeadUse", player);
					return;
				}
			}
		}
	}
}
