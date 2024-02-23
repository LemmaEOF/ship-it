package dev.emi.shipit;

import dev.emi.shipit.registry.*;
import net.fabricmc.api.ModInitializer;

public class ShipItMain implements ModInitializer {

	@Override
	public void onInitialize() {
		ShipItBlocks.init();
		ShipItItems.init();
		ShipItBlockEntities.init();
		ShipItPOIs.init();
		ShipItVillagerProfessions.init();
		ShipItScreenHandlers.init();
		ShipItPackets.init();
	}
}