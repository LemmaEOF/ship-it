package dev.emi.shipit.registry;

import dev.emi.shipit.block.MailBoxBlock;
import dev.emi.shipit.block.PackageBlock;
import dev.emi.shipit.block.PostBoxBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ShipItBlocks {
	public static final Block POST_BOX = register("post_box", new PostBoxBlock(FabricBlockSettings.copyOf(Blocks.LOOM)));
	public static final Block MAIL_BOX = register("mail_box", new MailBoxBlock(FabricBlockSettings.copyOf(Blocks.LOOM)));
	public static final Block PACKAGE = register("package", new PackageBlock(FabricBlockSettings.copyOf(Blocks.LOOM)), new Item.Settings().maxCount(1));
	
	public static void init() {
	}

	private static Block register(String name, Block block) {
		return register(name, block, new Item.Settings());
	}

	private static Block register(String name, Block block, Item.Settings settings) {
		block = Registry.register(Registries.BLOCK, new Identifier("shipit", name), block);
		Registry.register(Registries.ITEM, new Identifier("shipit", name), new BlockItem(block, settings));
		return block;
	}
}
