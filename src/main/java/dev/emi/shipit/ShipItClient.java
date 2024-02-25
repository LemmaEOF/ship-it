package dev.emi.shipit;

import dev.emi.shipit.data.StationeryManager;
import dev.emi.shipit.registry.ShipItPackets;
import dev.emi.shipit.registry.ShipItScreens;
import dev.emi.shipit.screen.LetterEditScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ShipItClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ShipItScreens.init();
		//TODO: client packets class
		ClientPlayNetworking.registerGlobalReceiver(
				ShipItPackets.SYNC_STATIONERY,
				(client, handler, buf, responseSender) -> StationeryManager.INSTANCE.readPacket(buf)
		);
	}

	public static void openLetterScreen(PlayerEntity user, ItemStack stack, Hand hand) {
		MinecraftClient.getInstance().setScreen(new LetterEditScreen(user, stack, hand));
	}
}
