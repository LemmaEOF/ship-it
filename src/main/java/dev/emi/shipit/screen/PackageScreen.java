package dev.emi.shipit.screen;

import dev.emi.shipit.screen.handler.PackageScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

//TODO: make it look more like a package? that'd be cool
//TODO: render what stamps are on the box
public class PackageScreen extends HandledScreen<PackageScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/dispenser.png");

	public PackageScreen(PackageScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
	}


	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
//		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//		this.client.getTextureManager().bindTexture(TEXTURE);
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		int stamps = handler.stamps;
		if (stamps > 0) {
			context.drawTexture(TEXTURE, x + 115, y + 16, 61, 16, 18 * stamps, 54);
			context.drawTexture(TEXTURE, x + 61 - 18 * stamps, y + 16, 61, 16, 18 * stamps, 54);
		}
	}
}
