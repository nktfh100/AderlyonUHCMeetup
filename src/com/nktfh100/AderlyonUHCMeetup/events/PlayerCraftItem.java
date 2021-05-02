package com.nktfh100.AderlyonUHCMeetup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import com.nktfh100.AderlyonUHCMeetup.enums.ScenarioType;
import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;
import com.nktfh100.AderlyonUHCMeetup.utils.XMaterial;

public class PlayerCraftItem implements Listener {
	@EventHandler
	public void onCraft(CraftItemEvent ev) {
		if (UHCMeetup.getInstance().getGameManager().getActiveScenario() == ScenarioType.BOWLESS && ev.getRecipe().getResult().getType() == XMaterial.BOW.parseMaterial()) {
			ev.setCancelled(true);
		} else if (UHCMeetup.getInstance().getGameManager().getActiveScenario() == ScenarioType.RODLESS && ev.getRecipe().getResult().getType() == XMaterial.FISHING_ROD.parseMaterial()) {
			ev.setCancelled(true);
		}
	}
}
