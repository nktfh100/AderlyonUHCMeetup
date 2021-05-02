package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.enums.ScenarioType;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.Utils;
import com.nktfh100.AderlyonUHCMeetup.utils.XMaterial;

public class PlayerDeath implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerDeathEvent(PlayerDeathEvent ev) {
		if (ev.getEntity() instanceof Player && UHCMeetup.getInstance().getGameManager().getGameState() == GameState.RUNNING) {
			final Player player = ev.getEntity();
			ev.setDeathMessage(null);
			Boolean droppedHead = false;

			if (UHCMeetup.getInstance().getGameManager().getActiveScenario() == ScenarioType.TNTONDEATH) {
				UHCMeetup.getInstance().getWorldManager().getWorld().spawnEntity(player.getLocation().add(0, 1.5, 0), EntityType.PRIMED_TNT);
			} else if (UHCMeetup.getInstance().getGameManager().getActiveScenario() == ScenarioType.TIMEBOMB) {
				System.out.println("Time bomb");
				Block chestBlock = player.getLocation().getBlock();
				chestBlock.setType(XMaterial.CHEST.parseMaterial());
				Chest chest = (Chest) chestBlock.getState();
				chest.update();
				if (UHCMeetup.getInstance().getConfigManager().getDropGoldenHead()) {
					chest.getInventory().addItem(UHCMeetup.getInstance().getItemsManager().getItem("goldenHead").getItem().getItem());
					droppedHead = true;
				}
				for (ItemStack item_ : ev.getDrops()) {
					chest.getInventory().addItem(item_.clone());
				}
				ev.getDrops().clear();
				// Explode after 30 seconds
				new BukkitRunnable() {
					@Override
					public void run() {
						chest.getInventory().clear();
						UHCMeetup.getInstance().getWorldManager().getWorld().createExplosion(chestBlock.getLocation(), 4F);
					}
				}.runTaskLater(UHCMeetup.getInstance(), 20L * 30L);
			}
			if (!droppedHead && UHCMeetup.getInstance().getConfigManager().getDropGoldenHead()) {
				player.getLocation().getWorld().dropItem(player.getLocation(), UHCMeetup.getInstance().getItemsManager().getItem("goldenHead").getItem().getItem());
			}
			Player killer = ev.getEntity().getKiller();
			Utils.strikeLightning(player.getLocation());
			Integer distance = Utils.getDistance(player, killer);
			new BukkitRunnable() {
				@Override
				public void run() {
					player.spigot().respawn();
					UHCMeetup.getInstance().getGameManager().playerDeath(player, killer, distance);
				}
			}.runTaskLater(UHCMeetup.getInstance(), 2L);

		}
	}
}
