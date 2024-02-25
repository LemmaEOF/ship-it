package dev.emi.shipit.registry;

import dev.emi.shipit.data.StationeryManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;

public class ShipItData {

	public static void init() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(StationeryManager.INSTANCE);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
			PacketByteBuf buf = PacketByteBufs.create();
			StationeryManager.INSTANCE.writePacket(buf);
			ServerPlayNetworking.send(player, ShipItPackets.SYNC_STATIONERY, buf);
		});
	}
}
