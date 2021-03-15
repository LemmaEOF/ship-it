package dev.emi.shipit.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.emi.shipit.component.PlayerMailInfo;
import dev.emi.shipit.registry.ShipItComponents;
import dev.emi.shipit.registry.ShipItPackets;
import dev.emi.shipit.screen.handler.PostBoxScreenHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PostBoxScreen extends HandledScreen<PostBoxScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("shipit", "textures/gui/container/post_box.png");
	private TextFieldWidget searchWidget;
	private List<PlayerMailInfo> playerSearch = new ArrayList<>();
	private UUID selected;
	private int scrollOffset;

	public PostBoxScreen(PostBoxScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundHeight = 183;
		this.titleY = Integer.MIN_VALUE;
		this.playerInventoryTitleY = Integer.MIN_VALUE;
	}

	protected void init() {
		super.init();

		this.client.keyboard.setRepeatEvents(true);
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;

		this.searchWidget = new TextFieldWidget(this.textRenderer, x + 60, y + 11, 108, 12, new LiteralText("asdf"));
		this.searchWidget.setFocusUnlocked(false);
		this.searchWidget.setEditableColor(-1);
		this.searchWidget.setUneditableColor(-1);
		this.searchWidget.setDrawsBackground(false);
		this.searchWidget.setMaxLength(16);
		this.searchWidget.setChangedListener(this::updateSearch);
		this.children.add(searchWidget);
		this.setInitialFocus(searchWidget);

		this.addButton(new ButtonWidget(x + 15, y + 30, 20, 20, new LiteralText("!"), (button) -> {
			if (selected == null) {
				return;
			}
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeInt(handler.syncId);
			buf.writeUuid(selected);
			MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(ShipItPackets.SEND_MAIL, buf));
		}));

		playerSearch = new ArrayList<>(ShipItComponents.MAIL.get(this.client.world.getLevelProperties()).getAllMailInfos().values());
	}

	private void updateSearch(String name) {
		String lower = name.toLowerCase();
		playerSearch = ShipItComponents.MAIL.get(this.client.world.getLevelProperties()).getAllMailInfos().values().stream()
			.filter(info -> info.name.toLowerCase().contains(lower)).collect(Collectors.toList());
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
		this.searchWidget.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		for (int i = 0; i < 3 && scrollOffset + i < playerSearch.size(); i++) {
			int vOff = 0;
			if (playerSearch.get(scrollOffset + i).uuid.equals(selected)) {
				vOff = 23;
			} else if (mouseX >= x + 58 && mouseX < x + 168 && mouseY >= y + 27 + i * 23 && mouseY < y + 50 + i * 23) {
				vOff = 46;
			}
			this.drawTexture(matrices, x + 58, y + 27 + i * 23, 0, 183 + vOff, 110, 23);
			DrawableHelper.drawStringWithShadow(matrices, textRenderer, playerSearch.get(scrollOffset + i).name, x + 74, y + 29 + i * 23, -1);
			DrawableHelper.drawStringWithShadow(matrices, textRenderer, "1123 Bun Street", x + 74, y + 39 + i * 23, 0xBBBBBB);
			this.client.getTextureManager().bindTexture(TEXTURE);
		}
		int scroll = 0;
		if (playerSearch.size() > 3) {
			scroll = 54 * scrollOffset / playerSearch.size() - 3;
		}
		this.drawTexture(matrices, x + 44, y + 27 + scroll, 176 + (playerSearch.size() > 3 ? 0 : 12), 0, 12, 15);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.player.closeHandledScreen();
		}
		return !this.searchWidget.keyPressed(keyCode, scanCode, modifiers) && !this.searchWidget.isActive() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		if (mouseX >= x + 58 && mouseX < x + 168 && mouseY > y + 27) {
			int n = ((int) mouseY - y - 27) / 23;
			if (n < 3) {
				if (scrollOffset + n < playerSearch.size()) {
					selected = playerSearch.get(scrollOffset + n).uuid;
				} else {
					selected = null;
				}
				MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
			}
		}
		if (searchWidget.isMouseOver(mouseX, mouseY)) {
			focusOn(searchWidget);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
}

