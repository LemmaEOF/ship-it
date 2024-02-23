package dev.emi.shipit.component;

import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class PlayerMailInfo implements Inventory {
	//TODO: data-drive
	private static final String[] STREET_NAMES = new String[] {
		"Bun", "Deer", "Powder", "Gamma", "Pear"
	};
	private static final String[] STREET_SUFFIXES = new String[] {
		"Drive", "Avenue", "Street", "Road", "Lane"
	};
	private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(9, ItemStack.EMPTY);
	public String name;
	public String address;
	public UUID uuid;
	// If the player has ever placed a mailbox
	public boolean placed;

	public PlayerMailInfo() {
	}

	public PlayerMailInfo(PlayerEntity player) {
		this.uuid = player.getUuid();
		this.name = player.getName().getString();
		generateRandomAddress();
	}

	public void tryNameUpdate(PlayerEntity player) {
		name = player.getName().getString();
	}

	private void generateRandomAddress() {
		Random random = new Random();
		address = random.nextInt(10000) + " " + STREET_NAMES[random.nextInt(STREET_NAMES.length)] + " "
			+ STREET_SUFFIXES[random.nextInt(STREET_SUFFIXES.length)];
	}

	public NbtCompound toTag() {
		NbtCompound tag = new NbtCompound();
		Inventories.writeNbt(tag, stacks, false);
		tag.putString("Name", name);
		tag.putString("Address", address);
		tag.putUuid("Uuid", uuid);
		tag.putBoolean("Placed", placed);
		return tag;
	}

	public void fromTag(NbtCompound tag) {
		if (tag.contains("Items", 9)) {
			Inventories.readNbt(tag, stacks);
		}
		name = tag.getString("Name");
		address = tag.getString("Address");
		uuid = tag.getUuid("Uuid");
		placed = tag.getBoolean("Placed");
	}

	public boolean isFull() {
		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void clear() {
		for (int i = 0; i < size(); i++) {
			setStack(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int size() {
		return stacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return stacks.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return Inventories.splitStack(this.stacks, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(stacks, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		stacks.set(slot, stack);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void markDirty() {
	}
}
