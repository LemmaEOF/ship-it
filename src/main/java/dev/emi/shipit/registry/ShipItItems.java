package dev.emi.shipit.registry;

import dev.emi.shipit.item.WritableLetterItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ShipItItems {
	public static final Item BUNDLE_OF_CARDBOARD = register("bundle_of_cardboard", new Item(new Item.Settings().maxCount(16)));
	public static final Item STAMP = register("stamp", new Item(new Item.Settings().maxCount(16)));
	public static final Item ADDRESS_PLACARD = register("address_placard", new Item(new Item.Settings()));
	public static final Item LETTER = register("letter", new WritableLetterItem(new Item.Settings().maxCount(1)));

	public static final ItemGroup GROUP = Registry.register(Registries.ITEM_GROUP, new Identifier("shipit", "shipit"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemGroup.shipit.shipit"))
					.icon(() -> new ItemStack(STAMP))
					.entries((displayContext, entries) -> {
						entries.add(BUNDLE_OF_CARDBOARD);
						entries.add(STAMP);
						entries.add(LETTER);
						entries.add(ADDRESS_PLACARD);
						entries.add(ShipItBlocks.MAIL_BOX);
						entries.add(ShipItBlocks.PACKAGE);
						entries.add(ShipItBlocks.POST_BOX);
					})
					.build()
	);

	public static void init() {
	}

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier("shipit", name), item);
	}
}
