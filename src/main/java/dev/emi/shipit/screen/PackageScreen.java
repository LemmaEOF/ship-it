package dev.emi.shipit.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.emi.shipit.screen.handler.PackageScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PackageScreen extends HandledScreen<PackageScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/dispenser.png");

	public PackageScreen(PackageScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		int stamps = handler.stamps;
		if (stamps > 0) {
			this.drawTexture(matrices, x + 115, y + 16, 61, 16, 18 * stamps, 54);
			this.drawTexture(matrices, x + 61 - 18 * stamps, y + 16, 61, 16, 18 * stamps, 54);
		}
	}
}
