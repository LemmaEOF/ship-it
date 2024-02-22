package dev.emi.shipit.registry;

import dev.emi.shipit.screen.MailBoxScreen;
import dev.emi.shipit.screen.PackageScreen;
import dev.emi.shipit.screen.PostBoxScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ShipItScreens {

	public static void init() {
		register(ShipItScreenHandlers.MAIL_BOX, MailBoxScreen::new);
		register(ShipItScreenHandlers.POST_BOX, PostBoxScreen::new);
		register(ShipItScreenHandlers.PACKAGE, PackageScreen::new);
	}

	public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<H> type, HandledScreens.Provider<H, S> supplier) {
		HandledScreens.register(type, supplier);
	}
}
