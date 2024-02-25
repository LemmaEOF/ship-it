package dev.emi.shipit;

import dev.emi.shipit.registry.*;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShipItMain implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("shipit");

	@Override
	public void onInitialize() {
		ShipItBlocks.init();
		ShipItItems.init();
		ShipItBlockEntities.init();
		ShipItPOIs.init();
		ShipItVillagerProfessions.init();
		ShipItScreenHandlers.init();
		ShipItData.init();
		ShipItPackets.init();

	}
}