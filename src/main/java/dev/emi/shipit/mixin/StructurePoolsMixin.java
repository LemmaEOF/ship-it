package dev.emi.shipit.mixin;

import com.mojang.datafixers.util.Pair;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

@Mixin(StructurePools.class)
public class StructurePoolsMixin {
	@Unique
	private static final Identifier PLAINS = new Identifier("village/plains/houses");
	@Unique
	private static final Identifier DESERT = new Identifier("village/desert/houses");
	@Unique
	private static final Identifier SNOWY = new Identifier("village/snowy/houses");
	@Unique
	private static final Identifier TAIGA = new Identifier("village/taiga/houses");
	@Unique
	private static final Identifier SAVANNA = new Identifier("village/savanna/houses");
	
	@Inject(at = @At("HEAD"), method = "register")
	private static void register(StructurePool pool, CallbackInfoReturnable<StructurePool> info) {
		if (pool.getId().equals(PLAINS)) {
			addToPool(pool, "shipit:village/plains/houses/plains_post_office_1", 4);
		} else if (pool.getId().equals(DESERT)) {
			addToPool(pool, "shipit:village/desert/houses/desert_post_office_1", 4);
		} else if (pool.getId().equals(SNOWY)) {
			addToPool(pool, "shipit:village/snowy/houses/snowy_post_office_1", 4);
		} else if (pool.getId().equals(TAIGA)) {
			addToPool(pool, "shipit:village/taiga/houses/taiga_post_office_1", 4);
		} else if (pool.getId().equals(SAVANNA)) {
			addToPool(pool, "shipit:village/savanna/houses/savanna_post_office_1", 4);
		}
	}

	@Unique
	private static void addToPool(StructurePool pool, String name, int weight) {
		StructurePoolElement element = StructurePoolElement.method_30426(name, StructureProcessorLists.EMPTY)
			.apply(StructurePool.Projection.RIGID);
		for (int i = 0; i < weight; i++) {
			((StructurePoolAccessor) pool).getElements().add(element);
		}
		((StructurePoolAccessor) pool).getElementCounts().add(Pair.of(element, weight));
	}
}
