package com.nktfh100.AderlyonUHCMeetup.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nktfh100.AderlyonUHCMeetup.enums.GameState;

public class Utils {

	public static String locationToString(Location loc) {
		if (loc == null || loc.getWorld() == null) {
			return "()";
		}
		String output = "(" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")";
		return output;
	}

	public static ChatColor getStateColor(GameState state) {
		switch (state) {
		case LOADING:
			return ChatColor.YELLOW;
		case WAITING:
			return ChatColor.GREEN;
		case STARTING:
			return ChatColor.BLUE;
		case RUNNING:
			return ChatColor.RED;
		default:
			return ChatColor.WHITE;
		}
	}

	public static double calcAvarage(ArrayList<Double> allX) {
		double result = 0;
		for (double number : allX) {
			result = result + number;
		}
		return result / allX.size();
	}

	public static Boolean isInsideCircle(Location center, Integer r, Location loc) {
		Double output = (Math.pow(loc.getX() - center.getX(), 2) + Math.pow(loc.getZ() - center.getZ(), 2));
		Double r1 = Math.pow(r, 2);
		if (output == r1) { // on point
			return true;
		} else if (output < r1) { // inside
			return true;
		} else {
			return false; // outside
		}
	}

	public static Boolean isInsideTwoPoints(Location loc, Location corner1, Location corner2) {
		if ((loc.getX() >= corner1.getX()) && (loc.getX() <= corner2.getX()) || (loc.getX() <= corner1.getX()) && (loc.getX() >= corner2.getX())) {
			if ((loc.getZ() >= corner1.getZ()) && (loc.getZ() <= corner2.getZ()) || (loc.getZ() <= corner1.getZ()) && (loc.getZ() >= corner2.getZ())) {
				return true;
			}
		}
		return false;
	}

	public static Boolean isInsideTwoPointsY(Location loc, Location corner1, Location corner2) {
		if ((loc.getX() >= corner1.getX()) && (loc.getX() <= corner2.getX()) || (loc.getX() <= corner1.getX()) && (loc.getX() >= corner2.getX())) {
			if ((loc.getZ() >= corner1.getZ()) && (loc.getZ() <= corner2.getZ()) || (loc.getZ() <= corner1.getZ()) && (loc.getZ() >= corner2.getZ())) {
				if ((loc.getY() >= corner1.getY()) && (loc.getY() <= corner2.getY()) || (loc.getY() <= corner1.getY()) && (loc.getY() >= corner2.getY())) {
					return true;
				}
			}
		}
		return false;
	}

	public static ItemStack setItemName(ItemStack item, String name, String... lore) {
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			ArrayList<String> metaLore = new ArrayList<String>();

			for (String lorecomments : lore) {
				metaLore.add(lorecomments);
			}
			meta.setLore(metaLore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack setItemName(ItemStack item, String name, ArrayList<String> lore) {
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			if (name != null) {
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			}
			ArrayList<String> metaLore = new ArrayList<String>();

			for (String loreLine : lore) {
				metaLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
			}
			meta.setLore(metaLore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack enchantedItem(ItemStack item, Enchantment ench, int lvl) {
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(ench, lvl, true);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack createItem(Material mat, String name) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		if (name != null) {
			meta.setDisplayName(name);
		}
		item.setItemMeta(meta);
		return (item);
	}

	public static ItemStack createItem(Material mat, String name, int amount) {
		ItemStack item = createItem(mat, name);
		item.setAmount(amount);
		return (item);
	}

	public static ItemStack createItem(Material mat, String name, int amount, String... lore) {
		ItemStack item = createItem(mat, name, amount);
		ItemMeta meta = item.getItemMeta();

		ArrayList<String> metaLore = new ArrayList<String>();

		for (String lorecomments : lore) {
			metaLore.add(lorecomments);
		}
		meta.setLore(metaLore);
		item.setItemMeta(meta);
		return (item);
	}

	public static ItemStack createItem(Material mat, String name, int amount, ArrayList<String> lore) {
		ItemStack item = createItem(mat, name, amount);
		ItemMeta meta = item.getItemMeta();

		meta.setLore(lore);
		item.setItemMeta(meta);
		return (item);
	}

	public static ItemStack createEnchantedItem(Material mat, String name, Enchantment ench, int lvl, String... lore) {
		ItemStack item = createItem(mat, name, 1);
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(ench, lvl, true);
		ArrayList<String> metaLore = new ArrayList<String>();

		for (String lorecomments : lore) {
			metaLore.add(lorecomments);
		}
		meta.setLore(metaLore);
		item.setItemMeta(meta);
		return (item);
	}

	public static int getRandomNumberInRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public static String getRandomColors() {
		String str = "";
		String toPickFrom = "123456789abcdfe";

		for (int i = 0; i < getRandomNumberInRange(3, 5); i++) {
			String char_ = String.valueOf(toPickFrom.charAt(getRandomNumberInRange(0, toPickFrom.length() - 1)));
			str = str + "&" + char_;
		}
		str = ChatColor.translateAlternateColorCodes('&', str);
		return str;
	}

	public static String reverseString(String str) {

		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append(str);
		strBuilder = strBuilder.reverse();
		return strBuilder.toString();

	}

	public static void fillInv(Inventory inv, Material mat) {
		ItemStack item = Utils.createItem(mat, " ");
		for (int slot = 0; slot < inv.getSize(); slot++) {
			inv.setItem(slot, item);
		}
	}

	public static void fillInv(Inventory inv, ItemStack item) {
		for (int slot = 0; slot < inv.getSize(); slot++) {
			inv.setItem(slot, item);
		}
	}

	public static void addBorder(Inventory inv, Integer slots, Material mat) {
		ItemStack border = Utils.createItem(mat, " ");
		if (slots == 45) {
			for (int slot = 0; slot < inv.getSize(); slot++) {
				if (inv.getItem(slot) == null) {
					if ((slot >= 0 && slot <= 8) || (slot >= 36 && slot <= 44) || (slot % 9 == 0) || ((slot - 8) % 9 == 0)) {
						inv.setItem(slot, border);
					}
				}
			}
		} else if (slots == 27) {
			for (int slot = 0; slot < inv.getSize(); slot++) {
				if (inv.getItem(slot) == null) {
					if ((slot >= 0 && slot <= 8) || (slot >= 18 && slot <= 26) || (slot % 9 == 0) || ((slot - 8) % 9 == 0)) {
						inv.setItem(slot, border);
					}
				}
			}
		}
	}

	public static float getRandomFloat(float min, float max) {
		Random rand = new Random();
		return rand.nextFloat() * (max - min) + min;
	}

	public static ItemStack addItemFlag(ItemStack item, ItemFlag... flag) {
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(flag);
			item.setItemMeta(meta);
		}
		return item;
	}

	// https://github.com/DuffyScottC/GridCircleGenerator/blob/delop/src/dpr212/GenerateCircle.java
	public static ArrayList<Location> generateHollowCircle(Location loc, int radius, int height) {
		ArrayList<Location> coordinates = new ArrayList<Location>();
		int y = loc.getBlockY();
		for (int yi = 0; yi < height; yi++) {

			int x = radius;
			int z = 0;
			int err = 1 - x;

			while (x >= z) {

				coordinates.add(new Location(loc.getWorld(), x + loc.getBlockX(), y, -z + loc.getBlockZ()));
				coordinates.add(new Location(loc.getWorld(), z + loc.getBlockX(), y, -x + loc.getBlockZ()));
				coordinates.add(new Location(loc.getWorld(), -z + loc.getBlockX(), y, -x + loc.getBlockZ()));
				coordinates.add(new Location(loc.getWorld(), -x + loc.getBlockX(), y, -z + loc.getBlockZ()));
				coordinates.add(new Location(loc.getWorld(), -x + loc.getBlockX(), y, z + loc.getBlockZ()));
				coordinates.add(new Location(loc.getWorld(), -z + loc.getBlockX(), y, x + loc.getBlockZ()));
				coordinates.add(new Location(loc.getWorld(), z + loc.getBlockX(), y, x + loc.getBlockZ()));
				coordinates.add(new Location(loc.getWorld(), x + loc.getBlockX(), y, z + loc.getBlockZ()));

				z += 1;
				if (err <= 0) {
					err += 2 * z + 1;
				} else {
					x -= 1;
					err += 2 * (z - x) + 1;
				}

			}
			y++;
		}
		return coordinates;
	}

	public static boolean hasChangedBlockCoordinates(Location fromLoc, Location toLoc) {
		return !(fromLoc.getWorld().equals(toLoc.getWorld()) && fromLoc.getBlockX() == toLoc.getBlockX() && fromLoc.getBlockZ() == toLoc.getBlockZ());
	}

	public static boolean hasChangedBlockCoordinatesY(Location fromLoc, Location toLoc) {
		return !(fromLoc.getWorld().equals(toLoc.getWorld()) && fromLoc.getBlockX() == toLoc.getBlockX() && fromLoc.getBlockZ() == toLoc.getBlockZ() && fromLoc.getBlockY() == toLoc.getBlockY());
	}

	final static char[] chars_ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890".toCharArray();

	public static String getRandomString(int length) {
		String randomString = "";
		final Random random = new Random();
		for (int i = 0; i < length; i++) {
			randomString = randomString + chars_[random.nextInt(chars_.length)];
		}
		return randomString;
	}

	public static String locationToConfigString(Location loc) {
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
	}

	public static ArrayList<Location> getBorderBlocks(Location corner1, Location corner2, Integer height) {
		ArrayList<Location> result = new ArrayList<Location>();
		if (corner1 == null || corner2 == null) {
			return result;
		}
		World world = corner1.getWorld();
		double minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
		double minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
		double maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
		double maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

		for (double x = minX; x <= maxX; x++) {
			for (double z = minZ; z <= maxZ; z++) {
				Boolean ok = false;
				if (x == minX || x == maxX)
					ok = true;
				if (z == minZ || z == maxZ)
					ok = true;
				if (ok) {
					int highestY = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z));
					int x_ = (int) Math.floor(x);
					int z_ = (int) Math.floor(z);
					Location highestYLoc = new Location(world, x_, highestY, z_);
					Location highestYLoc2 = new Location(world, x_, highestY - 1, z_);
					if (highestYLoc.getBlock().getType() == XMaterial.CACTUS.parseMaterial() || highestYLoc2.getBlock().getType() == XMaterial.CACTUS.parseMaterial()) {
						result.add(new Location(world, x_, highestY, z_));
						for (int i = 1; i < 5; i++) {
							Location loc_ = new Location(world, x_, highestY - i, z_);
							if (loc_.getBlock().getType() == Material.AIR || loc_.getBlock().getType() == XMaterial.CACTUS.parseMaterial()) {
								result.add(loc_);
							}
						}
					} else if (highestYLoc.getBlock().getType() == XMaterial.WATER.parseMaterial() || highestYLoc2.getBlock().getType() == XMaterial.WATER.parseMaterial()) {
						result.add(new Location(world, x_, highestY, z_));
						for (int i = 1; i < 6; i++) {
							Location loc_ = new Location(world, x_, highestY - i, z_);
							if (loc_.getBlock().getType() == XMaterial.WATER.parseMaterial()) {
								result.add(loc_);
							}
						}
					}

					if (highestYLoc.getBlock().getType().toString().contains("LEAVES") || highestYLoc2.getBlock().getType().toString().contains("LEAVES")) {
						result.add(new Location(world, x_, highestY + 1, z_));
						Integer to_ = highestY - 10;
						for (int i = highestY - 1; i >= to_; i--) {
							Location loc_ = new Location(world, x_, i, z_);
							if (loc_.getBlock().getType().toString().contains("LEAVES") || loc_.getBlock().getType().toString().contains("LOG")) {
								to_--;
							}
							if (loc_.getBlock().getType() == Material.AIR || loc_.getBlock().getType().toString().contains("LOG")) {
								result.add(loc_);
							}
							if (loc_.getBlock().getType() != Material.AIR && !(loc_.getBlock().getType().toString().contains("LEAVES") && !loc_.getBlock().getType().toString().contains("LOG"))) {
								break;
							}
						}

					} else {
						result.add(new Location(world, x_, highestY, z_));
						result.add(new Location(world, x_, highestY - 1, z_));
						result.add(new Location(world, x_, highestY - 2, z_));
						for (int i = 0; i < height; i++) {
							int Y = highestY + i;
							result.add(new Location(world, x_, Y + 1, z_));
						}
					}
				}
			}
		}
		return result;
	}

	public static String getDeathMsgType(Player player, Player killer, EntityDamageEvent.DamageCause damageCause) {
		if (killer != null && killer.getName().equals(player.getName())) {
			return "death";
		}
		if (damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
			return "normal";
		} else if (damageCause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			if (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof Arrow) {
				return "bow";
			} else if (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof EnderPearl) {
				return "enderPearl";
			}
		} else if (damageCause.equals(EntityDamageEvent.DamageCause.FIRE)) {
			return "fire";
		} else if (damageCause.equals(EntityDamageEvent.DamageCause.LAVA)) {
			return "lava";
		} else if (damageCause.equals(EntityDamageEvent.DamageCause.FALL)) {
			return "fall";
		} else if (damageCause.equals(EntityDamageEvent.DamageCause.CONTACT)) {
			return "cactus";
		}
		return "death";
	}

	public static int getDistance(Player player, Player player2) {
		if (player2 != null && player.getWorld().getName().equals(player2.getWorld().getName())) {
			return (int) player.getLocation().distance(player2.getLocation());
		} else {
			return 0;
		}
	}

	public static void strikeLightning(Location loc) {
		loc.getWorld().strikeLightningEffect(loc);

		if (loc.getBlock().getType() == Material.FIRE) {
			loc.getBlock().setType(Material.AIR);
		}
	}

	public static ItemStack applyHeadTexture(ItemStack item, String texture) {
		if (texture == null || texture.isEmpty()) {
			return item;
		}
		SkullMeta headMeta = (SkullMeta) item.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", texture));

		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);

		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException err) {
			err.printStackTrace();
		}
		item.setItemMeta(headMeta);
		return item;
	}

}
