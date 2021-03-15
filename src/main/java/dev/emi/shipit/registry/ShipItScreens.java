package dev.emi.shipit.registry;

import dev.emi.shipit.screen.MailBoxScreen;
import dev.emi.shipit.screen.PackageScreen;
import dev.emi.shipit.screen.PostBoxScreen;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.data.client.model.BlockStateVariantMap.TriFunction;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

public class ShipItScreens {

	public static void init() {
		register(ShipItScreenHandlers.MAIL_BOX, MailBoxScreen::new);
		register(ShipItScreenHandlers.POST_BOX, PostBoxScreen::new);
		register(ShipItScreenHandlers.PACKAGE, PackageScreen::new);
	}

	public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<H> type, TriFunction<H, PlayerInventory, Text, S> supplier) {
		ScreenRegistry.register(type, new ScreenRegistry.Factory<H, S>() {
			public S create(H handler, PlayerInventory inventory, Text title) {
				return supplier.apply(handler, inventory, title);
			}
		});
	}
}
