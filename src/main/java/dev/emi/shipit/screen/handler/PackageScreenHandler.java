package dev.emi.shipit.screen.handler;

import dev.emi.shipit.registry.ShipItScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class PackageScreenHandler extends ScreenHandler {
	private Inventory pack;
	public int stamps;

	public PackageScreenHandler(int syncId, PlayerInventory inventory, int stamps) {
		this(syncId, inventory, stamps, new SimpleInventory(27));
	}

	public PackageScreenHandler(int syncId, PlayerInventory inventory, int stamps, Inventory pack) {
		super(ShipItScreenHandlers.PACKAGE, syncId);
		this.pack = pack;
		this.stamps = stamps;

		for (int y = 0; y < 3; y++) {
			for (int x = stamps; x > 0; x--) {
				this.addSlot(new Slot(pack, 3 + x * 6 + y * 2, 62 - x * 18, 17 + y * 18));
			}
			for (int x = 0; x < 3; x++) {
				this.addSlot(new Slot(pack, x + y * 3, 62 + x * 18, 17 + y * 18));
			}
			for (int x = 0; x < stamps; x++) {
				this.addSlot(new Slot(pack, 10 + x * 6 + y * 2, 116 + x * 18, 17 + y * 18));
			}
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}
		for (int x = 0; x < 9; x++) {
			this.addSlot(new Slot(inventory, x, 8 + x * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return pack.canPlayerUse(player);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slotId) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotId);
		if (slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			if (slotId < getCurrentSlotCount()) {
				if (!this.insertItem(slotStack, getCurrentSlotCount(), getCurrentSlotCount()+36, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(slotStack, 0, getCurrentSlotCount(), false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (slotStack.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, slotStack);
		}

		return stack;
	}

	private int getCurrentSlotCount() {
		return 9+(6*stamps);
	}
}