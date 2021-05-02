package com.nktfh100.AderlyonUHCMeetup.info;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.nktfh100.AderlyonUHCMeetup.main.UHCMeetup;

public class BorderStage {

	private int id;
	private Integer size;
	private Integer time;
	private ArrayList<Integer> messages;

	private double maxX;
	private double minX;
	private double maxZ;
	private double minZ;

	public BorderStage(int id, Integer size, Integer time, ArrayList<Integer> messages) {
		this.id = id;
		this.size = size;
		this.time = time;
		this.messages = messages;
		this.maxX = this.size;
		this.minX = -this.size;
		this.maxZ = this.size;
		this.minZ = -this.size;
	}

	public boolean insideBorder(double xLoc, double zLoc) {
		return !(xLoc < minX || xLoc > maxX || zLoc < minZ || zLoc > maxZ);
	}

	public void teleportPlayerInside(Player player) {
		World world = UHCMeetup.getInstance().getWorldManager().getWorld();
		Boolean tp = false;
		Location pLoc = player.getLocation();
		Integer newX = pLoc.getBlockX();
		Integer newZ = pLoc.getBlockZ();
		
		Random random = new Random();

		if (newX >= this.size - 2) {
			newX = this.size - (3 + random.nextInt(7));
			tp = true;
		}
		if (newZ >= this.size - 2) {
			newZ = this.size - (3 + random.nextInt(7));
			tp = true;
		}
		if (newX <= -(this.size - 2)) {
			newX = -this.size + (3 + random.nextInt(7));
			tp = true;
		}
		if (newZ <= -(this.size - 2)) {
			newZ = -this.size + (3 + random.nextInt(7));
			tp = true;
		}
		if (tp) {
			Location tpTo = new Location(world, newX + 0.5, world.getHighestBlockAt(newX, newZ).getLocation().getBlockY() + 2, newZ + 0.5, pLoc.getYaw(), pLoc.getPitch());
			player.teleport(tpTo);
		}
	}

	public Integer getSize() {
		return size;
	}

	public Integer getTime() {
		return time;
	}

	public ArrayList<Integer> getMessages() {
		return messages;
	}

	public int getId() {
		return id;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMinX() {
		return minX;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public double getMinZ() {
		return minZ;
	}
}
