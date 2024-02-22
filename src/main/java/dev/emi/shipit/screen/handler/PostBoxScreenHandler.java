package dev.emi.shipit.screen.handler;

import java.util.UUID;

import dev.emi.shipit.registry.ShipItComponents;
import dev.emi.shipit.registry.ShipItScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;

public class PostBoxScreenHandler extends ScreenHandler {
	public Inventory postInventory;

	public PostBoxScreenHandler(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, new SimpleInventory(1));
	}

	public PostBoxScreenHandler(int syncId, PlayerInventory inventory, Inventory post) {
		super(ShipItScreenHandlers.POST_BOX, syncId);
		this.postInventory = post;

		this.addSlot(new Slot(post, 0, 17, 67));

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 3; y++) {
				this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 101 + y * 18));
			}
			this.addSlot(new Slot(inventory, x, 8 + x * 18, 159));
		}
	}

	public void sendMail(MinecraftServer server, UUID uuid) {
		if (!postInventory.isEmpty()) {
			if (ShipItComponents.MAIL.get(server.getOverworld().getLevelProperties()).sendMail(/*server, */uuid, postInventory.getStack(0))) {
				postInventory.clear();
			}
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
