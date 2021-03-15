package dev.emi.shipit.registry;

import java.util.UUID;

import dev.emi.shipit.screen.handler.PostBoxScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ShipItPackets {
	public static final Identifier SEND_MAIL = new Identifier("shipit", "send_mail");

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(SEND_MAIL, (server, player, handler, buf, responseSender) -> {
			final int syncId = buf.readInt();
			final UUID uuid = buf.readUuid();
			server.execute(() -> {
				if (player.currentScreenHandler.syncId == syncId && player.currentScreenHandler instanceof PostBoxScreenHandler) {
					((PostBoxScreenHandler) player.currentScreenHandler).sendMail(server, uuid);
				}
			});
		});
	}
}
