package dev.emi.shipit.block.entity;

import dev.emi.shipit.block.PackageBlock;
import dev.emi.shipit.registry.ShipItBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class PackageBlockEntity extends BlockEntity implements Inventory, SidedInventory {
	private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);

	public PackageBlockEntity(BlockPos pos, BlockState state) {
		super(ShipItBlockEntities.PACKAGE, pos, state);
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		if (tag.contains("Items", 9)) {
			Inventories.readNbt(tag, stacks);
		}
	}

	@Override
	public void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		getInventoryTag(tag);
	}
	
	public NbtCompound getInventoryTag(NbtCompound tag) {
		return Inventories.writeNbt(tag, stacks, false);
	}

	@Override
	public void clear() {
		for (int i = 0; i < size(); i++) {
			setStack(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int size() {
		return 9 + (6 * getCachedState().get(PackageBlock.STAMPS));
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
	public int[] getAvailableSlots(Direction side) {
		return IntStream.range(0, side()).toArray();
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return slot < size();
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot < size();
	}

}
