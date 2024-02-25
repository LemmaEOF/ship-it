package dev.emi.shipit;

import dev.emi.shipit.data.StationeryManager;
import dev.emi.shipit.registry.ShipItPackets;
import dev.emi.shipit.registry.ShipItScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

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
}
