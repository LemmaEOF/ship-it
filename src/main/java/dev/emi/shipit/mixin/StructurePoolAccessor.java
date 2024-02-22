package dev.emi.shipit.mixin;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;


@Mixin(StructurePool.class)
public interface StructurePoolAccessor {
	
	@Accessor("elements")
	ObjectArrayList<StructurePoolElement> getElements();

	@Accessor("elementCounts")
	List<Pair<StructurePoolElement, Integer>> getElementCounts();
}
