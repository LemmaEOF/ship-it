package dev.emi.shipit.registry;

import dev.emi.shipit.block.entity.PackageBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ShipItBlockEntities {
	public static final BlockEntityType<PackageBlockEntity> PACKAGE = register("package", PackageBlockEntity::new, ShipItBlocks.PACKAGE);

	private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntityFactory<T> supplier, Block... blocks) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("shipit", name),
			BlockEntityType.Builder.create(supplier, blocks).build(null));
	}

	public static void init() {}
}
