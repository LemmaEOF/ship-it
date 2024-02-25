package dev.emi.shipit.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import dev.emi.shipit.data.Stationery;
import dev.emi.shipit.data.StationeryManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class LetterScreen extends Screen {
	public static final Contents EMPTY_PROVIDER = new Contents() {
		@Override
		public int getPageCount() {
			return 0;
		}

		@Override
		public StringVisitable getPageUnchecked(int index) {
			return StringVisitable.EMPTY;
		}

		@Override
		public Stationery getStationery() {
			return StationeryManager.INSTANCE.getBlank();
		}
	};
	public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
	protected static final int MAX_TEXT_WIDTH = 114;
	protected static final int MAX_TEXT_HEIGHT = 128;
	protected static final int WIDTH = 192;
	protected static final int HEIGHT = 192;
	private Contents contents;
	private int pageIndex;
	private List<OrderedText> cachedPage = Collections.emptyList();
	private int cachedPageIndex = -1;
	private Text pageIndexText = ScreenTexts.EMPTY;
	private PageTurnWidget nextPageButton;
	private PageTurnWidget previousPageButton;
	private final boolean pageTurnSound;

	public LetterScreen(Contents pageProvider) {
		this(pageProvider, true);
	}

	public LetterScreen() {
		this(EMPTY_PROVIDER, false);
	}

	private LetterScreen(Contents contents, boolean playPageTurnSound) {
		super(NarratorManager.EMPTY);
		this.contents = contents;
		this.pageTurnSound = playPageTurnSound;
	}

	public void setPageProvider(Contents pageProvider) {
		this.contents = pageProvider;
		this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getPageCount());
		this.updatePageButtons();
		this.cachedPageIndex = -1;
	}

	public boolean setPage(int index) {
		int clampedPage = MathHelper.clamp(index, 0, this.contents.getPageCount() - 1);
		if (clampedPage != this.pageIndex) {
			this.pageIndex = clampedPage;
			this.updatePageButtons();
			this.cachedPageIndex = -1;
			return true;
		} else {
			return false;
		}
	}

	protected boolean jumpToPage(int page) {
		return this.setPage(page);
	}

	@Override
	protected void init() {
		this.addCloseButton();
		this.addPageButtons();
	}

	protected void addCloseButton() {
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, 196, 200, 20).build());
	}

	protected void addPageButtons() {
		int x = (this.width - WIDTH) / 2;
		this.nextPageButton = this.addDrawableChild(new PageTurnWidget(x + 116, 159, true, button -> this.goToNextPage(), this.pageTurnSound));
		this.previousPageButton = this.addDrawableChild(new PageTurnWidget(x + 43, 159, false, button -> this.goToPreviousPage(), this.pageTurnSound));
		this.updatePageButtons();
	}

	private int getPageCount() {
		return this.contents.getPageCount();
	}

	protected void goToPreviousPage() {
		if (this.pageIndex > 0) {
			--this.pageIndex;
		}

		this.updatePageButtons();
	}

	protected void goToNextPage() {
		if (this.pageIndex < this.getPageCount() - 1) {
			++this.pageIndex;
		}

		this.updatePageButtons();
	}

	private void updatePageButtons() {
		this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
		this.previousPageButton.visible = this.pageIndex > 0;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else {
			switch(keyCode) {
				case 266:
					this.previousPageButton.onPress();
					return true;
				case 267:
					this.nextPageButton.onPress();
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		int pageWidth = (this.width - WIDTH) / 2;
		context.drawTexture(this.contents.getStationery().guiTexture(), pageWidth, 2, 0, 0, WIDTH, HEIGHT);
		if (this.cachedPageIndex != this.pageIndex) {
			StringVisitable stringVisitable = this.contents.getPage(this.pageIndex);
			this.cachedPage = this.textRenderer.wrapLines(stringVisitable, MAX_TEXT_WIDTH);
			Stationery stationery = contents.getStationery();
			this.pageIndexText = Text.translatable("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1))
					.styled(style -> style.withColor(stationery.textColor()).withFont(stationery.font()));
		}

		this.cachedPageIndex = this.pageIndex;
		int textWidth = this.textRenderer.getWidth(this.pageIndexText);
		int textColor = this.contents.getStationery().textColor().getRgb();
		context.drawText(this.textRenderer, this.pageIndexText, pageWidth - textWidth + WIDTH - 44, 18, textColor, false);
		int textHeight = Math.min(MAX_TEXT_HEIGHT / 9, this.cachedPage.size());

		for(int m = 0; m < textHeight; ++m) {
			OrderedText orderedText = this.cachedPage.get(m);
			context.drawText(this.textRenderer, orderedText, pageWidth + 36, 32 + m * 9, textColor, false);
		}

		Style style = this.getTextStyleAt(mouseX, mouseY);
		if (style != null) {
			context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
		}

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			Style style = this.getTextStyleAt(mouseX, mouseY);
			if (style != null && this.handleTextClick(style)) {
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean handleTextClick(Style style) {
		ClickEvent clickEvent = style.getClickEvent();
		if (clickEvent == null) {
			return false;
		} else if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
			String string = clickEvent.getValue();

			try {
				int i = Integer.parseInt(string) - 1;
				return this.jumpToPage(i);
			} catch (Exception var5) {
				return false;
			}
		} else {
			boolean bl = super.handleTextClick(style);
			if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.closeScreen();
			}

			return bl;
		}
	}

	protected void closeScreen() {
		this.client.setScreen(null);
	}

	@Nullable
	public Style getTextStyleAt(double x, double y) {
		if (this.cachedPage.isEmpty()) {
			return null;
		} else {
			int textX = MathHelper.floor(x - (double)((this.width - WIDTH) / 2) - 36.0);
			int textY = MathHelper.floor(y - 2.0 - 30.0);
			if (textX >= 0 && textY >= 0) {
				int textHeight = Math.min(MAX_TEXT_HEIGHT / 9, this.cachedPage.size());
				if (textX <= MAX_TEXT_WIDTH && textY < 9 * textHeight + textHeight) {
					int l = textY / 9;
					if (l < this.cachedPage.size()) {
						OrderedText orderedText = this.cachedPage.get(l);
						return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, textX);
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	static List<String> readPages(NbtCompound nbt) {
		Builder<String> builder = ImmutableList.builder();
		filterPages(nbt, builder::add);
		return builder.build();
	}

	public static void filterPages(NbtCompound nbt, Consumer<String> pageConsumer) {
		NbtList nbtList = nbt.getList("pages", NbtElement.STRING_TYPE).copy();
		IntFunction<String> filterFunc;
		if (MinecraftClient.getInstance().shouldFilterText() && nbt.contains("filtered_pages", NbtElement.COMPOUND_TYPE)) {
			NbtCompound tag = nbt.getCompound("filtered_pages");
			filterFunc = page -> {
				String string = String.valueOf(page);
				return tag.contains(string) ? tag.getString(string) : nbtList.getString(page);
			};
		} else {
			filterFunc = nbtList::getString;
		}

		for(int i = 0; i < nbtList.size(); ++i) {
			pageConsumer.accept(filterFunc.apply(i));
		}
	}

	//TODO: fix for letter
	public interface Contents {
		int getPageCount();

		StringVisitable getPageUnchecked(int index);

		Stationery getStationery();

		default StringVisitable getPage(int index) {
			return index >= 0 && index < this.getPageCount() ? this.getPageUnchecked(index) : StringVisitable.EMPTY;
		}

		static Contents create(ItemStack stack) {
			if (stack.isOf(Items.WRITTEN_BOOK)) {
				return new WrittenBookContents(stack);
			} else {
				return stack.isOf(Items.WRITABLE_BOOK) ? new WritableBookContents(stack) : EMPTY_PROVIDER;
			}
		}
	}

	public static class WritableBookContents implements Contents {
		private final List<String> pages;
		private final Stationery stationery;
		private final Style style;

		public WritableBookContents(ItemStack stack) {
			this.pages = getPages(stack);
			this.stationery = getStationery(stack);
			this.style = Style.EMPTY.withFont(stationery.font()).withColor(stationery.textColor());
		}

		private static List<String> getPages(ItemStack stack) {
			NbtCompound nbtCompound = stack.getNbt();
			return nbtCompound != null ? readPages(nbtCompound) : ImmutableList.of();
		}

		private static Stationery getStationery(ItemStack stack) {
			NbtCompound nbt = stack.getNbt();
			if (nbt != null) {
				return StationeryManager.INSTANCE.getStationery(new Identifier(nbt.getString("stationery")));
			} else {
				return StationeryManager.INSTANCE.getBlank();
			}
		}

		@Override
		public int getPageCount() {
			return this.pages.size();
		}

		@Override
		public Stationery getStationery() {
			return stationery;
		}

		@Override
		public StringVisitable getPageUnchecked(int index) {
			return StringVisitable.styled(this.pages.get(index), style);
		}
	}

	//TODO: fix for letter
	public static class WrittenBookContents implements Contents {
		private final List<String> pages;
		private final Stationery stationery;
		private final Style style;

		public WrittenBookContents(ItemStack stack) {
			this.pages = getPages(stack);
			this.stationery = getStationery(stack);
			this.style = Style.EMPTY.withFont(stationery.font()).withColor(stationery.textColor());
		}

		private static List<String> getPages(ItemStack stack) {
			NbtCompound nbtCompound = stack.getNbt();
			return WrittenBookItem.isValid(nbtCompound)
					? readPages(nbtCompound)
					: ImmutableList.of(Text.Serializer.toJson(Text.translatable("book.invalid.tag").formatted(Formatting.DARK_RED)));
		}

		private static Stationery getStationery(ItemStack stack) {
			NbtCompound nbt = stack.getNbt();
			if (nbt != null) {
				return StationeryManager.INSTANCE.getStationery(new Identifier(nbt.getString("stationery")));
			} else {
				return StationeryManager.INSTANCE.getBlank();
			}
		}

		@Override
		public int getPageCount() {
			return this.pages.size();
		}

		@Override
		public Stationery getStationery() {
			return stationery;
		}

		@Override
		public StringVisitable getPageUnchecked(int index) {
			String string = this.pages.get(index);

			try {
				StringVisitable stringVisitable = Text.Serializer.fromJson(string);
				if (stringVisitable != null) {
					return stringVisitable;
				}
			} catch (Exception var4) {
			}

			return StringVisitable.styled(string, style);
		}
	}
}
