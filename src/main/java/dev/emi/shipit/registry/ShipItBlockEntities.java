package dev.emi.shipit.registry;

import com.google.common.base.Supplier;

import dev.emi.shipit.block.entity.PackageBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ShipItBlockEntities {
	//public static final BlockEntityType<MailBoxBlockEntity> MAIL_BOX = register("mail_box", MailBoxBlockEntity::new, ShipItBlocks.MAIL_BOX);
	public static final BlockEntityType<PackageBlockEntity> PACKAGE = register("package", PackageBlockEntity::new, ShipItBlocks.PACKAGE);
	
	private static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<T> supplier, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("shipit", name),
			BlockEntityType.Builder.create(supplier, blocks).build(null));
	}
}
