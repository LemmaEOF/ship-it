package dev.emi.shipit.registry;

import java.util.function.BiFunction;

import dev.emi.shipit.screen.handler.MailBoxScreenHandler;
import dev.emi.shipit.screen.handler.PackageScreenHandler;
import dev.emi.shipit.screen.handler.PostBoxScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ShipItScreenHandlers {
	public static final ScreenHandlerType<MailBoxScreenHandler> MAIL_BOX = register("mail_box", MailBoxScreenHandler::new);
	public static final ScreenHandlerType<PostBoxScreenHandler> POST_BOX = register("post_box", PostBoxScreenHandler::new);
	public static final ScreenHandlerType<PackageScreenHandler> PACKAGE;

	static {
		PACKAGE = ScreenHandlerRegistry.registerExtended(new Identifier("shipit", "package"), new ScreenHandlerRegistry.ExtendedClientHandlerFactory<PackageScreenHandler>(){
			public PackageScreenHandler create(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
				return new PackageScreenHandler(syncId, inventory, buf.readInt());
			}
		});
	}

	public static void init() {
	}
	
	public static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, BiFunction<Integer, PlayerInventory, T> supplier) {
		return ScreenHandlerRegistry.registerSimple(new Identifier("shipit", name), new ScreenHandlerRegistry.SimpleClientHandlerFactory<T>() {
			public T create(int syncId, PlayerInventory inventory) {
				return supplier.apply(syncId, inventory);
			}
		});
	}
}
