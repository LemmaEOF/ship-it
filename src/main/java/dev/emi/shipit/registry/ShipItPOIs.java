package dev.emi.shipit.registry;

import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class ShipItPOIs {

	public static final RegistryKey<PointOfInterestType> MAILROOM_KEY = RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier("shipit", "mailroom"));
	
	public static final PointOfInterestType MAILROOM = PointOfInterestHelper.register(new Identifier("shipit", "mailroom"), 1, 1,
		ImmutableSet.copyOf(ShipItBlocks.POST_BOX.getStateManager().getStates()));

	public static void init() {}
}
