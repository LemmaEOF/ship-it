package dev.emi.shipit.registry;

import java.util.UUID;

import dev.emi.shipit.data.StationeryManager;
import dev.emi.shipit.screen.handler.PostBoxScreenHandler;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.level.LevelComponents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ShipItPackets {
	public static final Identifier SEND_MAIL = new Identifier("shipit", "send_mail");
	public static final Identifier SYNC_STATIONERY = new Identifier("shipit", "sync_stationery");

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(SEND_MAIL, (server, player, handler, buf, responseSender) -> {
			final int syncId = buf.readInt();
			final UUID uuid = buf.readUuid();
			server.execute(() -> {
				if (player.currentScreenHandler.syncId == syncId && player.currentScreenHandler instanceof PostBoxScreenHandler) {
					((PostBoxScreenHandler) player.currentScreenHandler).sendMail(server, uuid);
					LevelComponents.sync(ShipItComponents.MAIL, server, (AutoSyncedComponent) ShipItComponents.MAIL.get(server.getOverworld().getLevelProperties()), p -> p == player);
				}
			});
		});
	}
}
