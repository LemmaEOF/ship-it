package dev.emi.shipit;

import dev.emi.shipit.registry.ShipItScreens;
import net.fabricmc.api.ClientModInitializer;

public class ShipItClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ShipItScreens.init();
	}
}
