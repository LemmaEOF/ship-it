package dev.emi.shipit;

import dev.emi.shipit.registry.ShipItBlocks;
import dev.emi.shipit.registry.ShipItItems;
import dev.emi.shipit.registry.ShipItPOIs;
import dev.emi.shipit.registry.ShipItPackets;
import dev.emi.shipit.registry.ShipItScreenHandlers;
import dev.emi.shipit.registry.ShipItVillagerProfessions;
import net.fabricmc.api.ModInitializer;

public class ShipItMain implements ModInitializer {

	@Override
	public void onInitialize() {
		ShipItBlocks.init();
		ShipItItems.init();
		ShipItPOIs.init();
		ShipItVillagerProfessions.init();
		ShipItScreenHandlers.init();
		ShipItPackets.init();
	}
}