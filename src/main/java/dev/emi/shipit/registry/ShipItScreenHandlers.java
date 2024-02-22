package dev.emi.shipit.registry;

import java.util.function.BiFunction;

import dev.emi.shipit.screen.handler.MailBoxScreenHandler;
import dev.emi.shipit.screen.handler.PackageScreenHandler;
import dev.emi.shipit.screen.handler.PostBoxScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ShipItScreenHandlers {
	public static final ScreenHandlerType<MailBoxScreenHandler> MAIL_BOX = register("mail_box", MailBoxScreenHandler::new);
	public static final ScreenHandlerType<PostBoxScreenHandler> POST_BOX = register("post_box", PostBoxScreenHandler::new);
	public static final ScreenHandlerType<PackageScreenHandler> PACKAGE;

	static {
		PACKAGE = Registry.register(Registries.SCREEN_HANDLER, new Identifier("shipit", "package"),
				new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> new PackageScreenHandler(syncId, inventory, buf.readInt())));
	}

	public static void init() {
	}
	
	public static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, BiFunction<Integer, PlayerInventory, T> supplier) {
		return Registry.register(Registries.SCREEN_HANDLER, new Identifier("shipit", name),
				new ScreenHandlerType<>(supplier::apply, FeatureSet.empty()));
	}
}
