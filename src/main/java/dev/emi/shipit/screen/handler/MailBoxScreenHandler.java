package dev.emi.shipit.screen.handler;

import dev.emi.shipit.registry.ShipItScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class MailBoxScreenHandler extends ScreenHandler {

	public MailBoxScreenHandler(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, new SimpleInventory(9));
	}

	public MailBoxScreenHandler(int syncId, PlayerInventory inventory, Inventory mail) {
		super(ShipItScreenHandlers.MAIL_BOX, syncId);
		
		for (int x = 0; x < 3; x++) {
		   for (int y = 0; y < 3; y++) {
			  this.addSlot(new Slot(mail, x + y * 3, 62 + x * 18, 17 + y * 18));
		   }
		}

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 3; y++) {
				this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
			this.addSlot(new Slot(inventory, x, 8 + x * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	//TODO: help
	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return null;
	}
}