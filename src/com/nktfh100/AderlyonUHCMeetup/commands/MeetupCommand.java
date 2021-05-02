package com.nktfh100.AderlyonUHCMeetup.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nktfh100.AderlyonUHCMeetup.enums.GameState;
import com.nktfh100.AderlyonUHCMeetup.inventory.StatsInv;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class MeetupCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
			sender.sendMessage("       " + ChatColor.GOLD + "" + ChatColor.BOLD + "Aderlyon UHC Meetup");
			if (!UHCMeetup.getInstance().getIsLobby() && sender.hasPermission("aderlyon-uhc-meetup.admin")) {
				sender.sendMessage(ChatColor.YELLOW + "/uhcmeetup start" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Force start the game");
				sender.sendMessage(ChatColor.YELLOW + "/uhcmeetup setlobby" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Set the waiting lobby location");
			}
			sender.sendMessage(ChatColor.YELLOW + "/uhcmeetup stats" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Show your stats");

			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
			return true;
		} else {
			if (UHCMeetup.getInstance().getIsLobby()) {
				return false;
			}
			if (args[0].equalsIgnoreCase("stats") && sender instanceof Player) {
				if (UHCMeetup.getInstance().getGameManager().getGameState() != GameState.RUNNING) {
					Player player = (Player) sender;
					player.openInventory(new StatsInv(UHCMeetup.getInstance().getPlayersManager().getPlayerInfo(player)).getInventory());
				}
				return true;
			}
			if (sender.hasPermission("aderlyon-uhc-meetup.admin")) {
				if (args[0].equalsIgnoreCase("start")) {
					if (UHCMeetup.getInstance().getGameManager().getGameState() == GameState.RUNNING || UHCMeetup.getInstance().getGameManager().getGameState() == GameState.LOADING) {
						sender.sendMessage(UHCMeetup.getInstance().getConfigManager().getPrefix() + ChatColor.RED + "Can't start the game now!");
					} else {
						UHCMeetup.getInstance().getGameManager().startGame();
					}
					return true;
				} else if (sender instanceof Player && args[0].equalsIgnoreCase("setlobby")) {
					Player player = (Player) sender;
					Location loc = player.getLocation();
					UHCMeetup.getInstance().getConfigManager().setWaitingLobby(loc);
					String locStr = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getYaw() + "," + loc.getPitch();
					UHCMeetup.getInstance().getConfig().set("waitingLobby", locStr);
					UHCMeetup.getInstance().saveConfig();
					player.sendMessage(UHCMeetup.getInstance().getConfigManager().getPrefix() + ChatColor.GREEN + "Changed waiting lobby to your location!");
					return true;
				}
			} else {
				sender.sendMessage(UHCMeetup.getInstance().getConfigManager().getPrefix() + ChatColor.RED + "You don't have permission to do that!");
			}
		}
		return false;
	}

}
