package com.nktfh100.AderlyonUHCMeetup.inventory;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.AderlyonUHCMeetup.enums.ScenarioType;
import com.nktfh100.AderlyonUHCMeetup.info.ItemInfo;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class ScenarioVoteInv extends CustomHolder {

	private HashMap<String, ScenarioType> playersVotes = new HashMap<String, ScenarioType>();
	private HashMap<ScenarioType, Integer> votes = new HashMap<ScenarioType, Integer>();

	public ScenarioVoteInv() {
		super(9, UHCMeetup.getInstance().getMessagesManager().getGameMsg("scenarioVoteTitle"));
		this.playersVotes = new HashMap<String, ScenarioType>();
		this.votes = new HashMap<ScenarioType, Integer>();
		for (ScenarioType st : ScenarioType.values()) {
			this.votes.put(st, 0);
		}
		this.update();
	}

	public void update() {
		ScenarioVoteInv inv = this;

		for (ScenarioType st : ScenarioType.values()) {
			Integer votes = this.votes.get(st);
			ItemInfo itemInfo = UHCMeetup.getInstance().getItemsManager().getItem("scenario_" + st.getName()).getItem();
			ItemStack item = itemInfo.getItem(votes + "", "");

			Icon icon = new Icon(item);
			icon.addClickAction(new ClickAction() {
				@Override
				public void execute(Player player) {
					inv.scenarioClick(player, st);
				}
			});
			this.setIcon(itemInfo.getSlot(), icon);
		}
	}

	public void scenarioClick(Player player, ScenarioType st) {
		this.votes.put(st, this.votes.get(st) + 1);
		if (this.playersVotes.get(player.getName()) == null) {
			Bukkit.broadcastMessage(UHCMeetup.getInstance().getMessagesManager().getGameMsg("playerVoted", player.getName()));
		} else {
			this.votes.put(this.playersVotes.get(player.getName()), this.votes.get(this.playersVotes.get(player.getName())) - 1);
		}
		this.playersVotes.put(player.getName(), st);
		player.sendMessage(UHCMeetup.getInstance().getMessagesManager().getGameMsg("scenarioVote", player.getName(), UHCMeetup.getInstance().getMessagesManager().getScenarioName(st.getName())));
		UHCMeetup.getInstance().getSoundsManager().playSound("playerScenarioVote", player);
		player.closeInventory();
	}

	public void openInv(Player player) {
		this.update();
		player.openInventory(this.getInventory());
	}

	public HashMap<ScenarioType, Integer> getVotes() {
		return this.votes;
	}

	public void playerLeft(Player player) {
		if (this.playersVotes.get(player.getName()) != null) {
			this.votes.put(this.playersVotes.get(player.getName()), this.votes.get(this.playersVotes.get(player.getName())) - 1);
		}
		this.playersVotes.put(player.getName(), null);
	}

}