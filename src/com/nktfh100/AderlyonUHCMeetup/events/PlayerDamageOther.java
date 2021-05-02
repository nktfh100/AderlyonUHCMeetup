package com.nktfh100.AderlyonUHCMeetup.events;

import java.text.DecimalFormat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.enums.ScenarioType;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class PlayerDamageOther implements Listener {

	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent ev) {
		if (ev.getDamager() instanceof Player && UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
			ev.setCancelled(true);
			return;
		}

		if (ev.getDamager() instanceof Player && ev.getEntity() instanceof Player) {
			Player victim = (Player) ev.getEntity();
			Player damager = (Player) ev.getDamager();
			if (UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(damager).isSpectating() || UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(victim).isSpectating()) {
				ev.setCancelled(true);
				return;
			}
		}

		if (ev.getEntity() instanceof Player && ev.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) ev.getDamager();
			final Player victim = (Player) ev.getEntity();
			if (projectile.getShooter() instanceof Player) {
				final Player attacker = (Player) projectile.getShooter();

				DecimalFormat numberFormat = new DecimalFormat("#.0");

				Double health = victim.getHealth() - ev.getFinalDamage();
				if (health > 0) {
					for (String line : UHCMeetup.getInstance().getMessagesManager()
							.getGameMsg("bowHit", victim.getName(), numberFormat.format(health), numberFormat.format(ev.getFinalDamage()) + "", null).split("/n")) {
						attacker.sendMessage(line);
					}
				}
				if (UHCMeetup.getInstance().getGameManager().getActiveScenario() == ScenarioType.SWITCHEROO) {
					Location player1Loc = victim.getLocation();
					Location player2Loc = attacker.getLocation();
					victim.teleport(player2Loc);
					attacker.teleport(player1Loc);
					victim.sendMessage(UHCMeetup.getInstance().getMessagesManager().getGameMsg("switcheroo", attacker.getName()));
					attacker.sendMessage(UHCMeetup.getInstance().getMessagesManager().getGameMsg("switcheroo", victim.getName()));
				}
			}
		}
	}
}
