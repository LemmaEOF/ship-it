package dev.emi.shipit.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ShipItItems {
	public static final Item BUNDLE_OF_CARDBOARD = register("bundle_of_cardboard", new Item(new Item.Settings().maxCount(16)));
	public static final Item STAMP = register("stamp", new Item(new Item.Settings().maxCount(16)));
	public static final Item ADDRESS_PLACARD = register("address_placard", new Item(new Item.Settings()));

	public static void init() {
	}

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier("shipit", name), item);
	}
}
