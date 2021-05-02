package com.nktfh100.AderlyonUHCMeetup.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class MeetupCommandTab implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> COMMANDS = new ArrayList<>();
		int arg = 0;
		if (args.length == 1) {
			arg = 0;
			if (sender.hasPermission("aderlyon-uhc-meetup.admin") && !UHCMeetup.getInstance().getIsLobby()) {
				COMMANDS.add("start");
				COMMANDS.add("setlobby");
			}
			COMMANDS.add("stats");
		}

		final List<String> completions = new ArrayList<>();
		StringUtil.copyPartialMatches(args[arg], COMMANDS, completions);

		Collections.sort(completions);
		return completions;
	}

}
