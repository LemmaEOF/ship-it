package dev.emi.shipit.block.entity;

import dev.emi.shipit.registry.ShipItBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

public class PackageBlockEntity extends BlockEntity implements Inventory {
	private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);

	public PackageBlockEntity() {
		super(ShipItBlockEntities.PACKAGE);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		if (tag.contains("Items", 9)) {
			Inventories.fromTag(tag, stacks);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		getInventoryTag(tag);
		return tag;
	}
	
	public CompoundTag getInventoryTag(CompoundTag tag) {
		return Inventories.toTag(tag, stacks, false);
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
}
