package dev.emi.shipit.screen;

import com.google.common.collect.Lists;
import dev.emi.shipit.data.Stationery;
import dev.emi.shipit.data.StationeryManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

//I'm sorry mojang
public class LetterEditScreen extends Screen {
	private static final int MAX_TEXT_WIDTH = 114;
	private static final int MAX_TEXT_HEIGHT = 128;
	private static final int WIDTH = 192;
	private static final int HEIGHT = 192;
	private static final Text EDIT_TITLE_TEXT = Text.translatable("book.editTitle");
	private static final Text FINALIZE_WARNING_TEXT = Text.translatable("shipit.letter.finalizeWarning");
	private static final OrderedText BLACK_CURSOR_TEXT = OrderedText.styledForwardsVisitedString("_", Style.EMPTY.withColor(Formatting.BLACK));
	private static final OrderedText GRAY_CURSOR_TEXT = OrderedText.styledForwardsVisitedString("_", Style.EMPTY.withColor(Formatting.GRAY));
	private final PlayerEntity player;
	private final ItemStack itemStack;
	private boolean dirty;
	private boolean sealing;
	private int tickCounter;
	private int currentPage;
	private final List<String> pages = Lists.newArrayList();
	private final SelectionManager currentPageSelectionManager = new SelectionManager(
			this::getCurrentPageContent,
			this::setPageContent,
			this::getClipboard,
			this::setClipboard,
			string -> string.length() < 1024 && this.textRenderer.getWrappedLinesHeight(string, MAX_TEXT_WIDTH) <= MAX_TEXT_HEIGHT
	);
	private long lastClickTime;
	private int lastClickIndex = -1;
	private PageTurnWidget nextPageButton;
	private PageTurnWidget previousPageButton;
	private ButtonWidget doneButton;
	private ButtonWidget sealButton;
	private ButtonWidget finalizeButton;
	private ButtonWidget cancelButton;
	private final Hand hand;
	@Nullable
	private PageContent pageContent = PageContent.EMPTY;
	private Text pageIndicatorText = ScreenTexts.EMPTY;
	private final Text fromText;
	private final Stationery stationery;

	public LetterEditScreen(PlayerEntity player, ItemStack itemStack, Hand hand) {
		super(NarratorManager.EMPTY);
		this.player = player;
		this.itemStack = itemStack;
		this.hand = hand;
		NbtCompound nbt = itemStack.getNbt();
		if (nbt != null) {
			BookScreen.filterPages(nbt, this.pages::add);
			this.stationery = StationeryManager.INSTANCE.getStationery(new Identifier(nbt.getString("stationery")));
		} else {
			this.stationery = StationeryManager.INSTANCE.getBlank();
		}

		if (this.pages.isEmpty()) {
			this.pages.add("");
		}

		this.fromText = Text.translatable("shipit.letter.from", player.getName()).formatted(Formatting.DARK_GRAY);
	}

	private void setClipboard(String clipboard) {
		if (this.client != null) {
			SelectionManager.setClipboard(this.client, clipboard);
		}
	}

	private String getClipboard() {
		return this.client != null ? SelectionManager.getClipboard(this.client) : "";
	}

	private int countPages() {
		return this.pages.size();
	}

	@Override
	public void tick() {
		super.tick();
		++this.tickCounter;
	}

	@Override
	protected void init() {
		this.invalidatePageContent();
		this.sealButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("shipit.letter.sealButton"), button -> {
			this.sealing = true;
			this.updateButtons();
		}).dimensions(this.width / 2 - 100, 196, 98, 20).build());
		this.doneButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
			this.client.setScreen(null);
			this.saveLetter(false);
		}).dimensions(this.width / 2 + 2, 196, 98, 20).build());
		this.finalizeButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("shipit.letter.finalizeButton"), button -> {
			if (this.sealing) {
				this.saveLetter(true);
				this.client.setScreen(null);
			}
		}).dimensions(this.width / 2 - 100, 196, 98, 20).build());
		this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
			if (this.sealing) {
				this.sealing = false;
			}
			this.updateButtons();
		}).dimensions(this.width / 2 + 2, 196, 98, 20).build());
		int x = (this.width - WIDTH) / 2;
		this.nextPageButton = this.addDrawableChild(new PageTurnWidget(x + 116, 159, true, button -> this.openNextPage(), true));
		this.previousPageButton = this.addDrawableChild(new PageTurnWidget(x + 43, 159, false, button -> this.openPreviousPage(), true));
		this.updateButtons();
	}

	private void openPreviousPage() {
		if (this.currentPage > 0) {
			--this.currentPage;
		}

		this.updateButtons();
		this.changePage();
	}

	private void openNextPage() {
		if (this.currentPage < this.countPages() - 1) {
			++this.currentPage;
		} else {
			this.appendNewPage();
			if (this.currentPage < this.countPages() - 1) {
				++this.currentPage;
			}
		}

		this.updateButtons();
		this.changePage();
	}

	private void updateButtons() {
		this.previousPageButton.visible = !this.sealing && this.currentPage > 0;
		this.nextPageButton.visible = !this.sealing;
		this.doneButton.visible = !this.sealing;
		this.sealButton.visible = !this.sealing;
		this.cancelButton.visible = this.sealing;
		this.finalizeButton.visible = this.sealing;
	}

	private void removeEmptyPages() {
		ListIterator<String> iter = this.pages.listIterator(this.pages.size());

		while(iter.hasPrevious() && iter.previous().isEmpty()) {
			iter.remove();
		}
	}

	//TODO: adapt for letters
	private void saveLetter(boolean seal) {
		if (this.dirty) {
			this.removeEmptyPages();
			this.writeNbtData(seal);
			int slot = this.hand == Hand.MAIN_HAND ? this.player.getInventory().selectedSlot : 40;
			this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(slot, this.pages, seal ? Optional.of("") : Optional.empty()));
		}
	}

	private void writeNbtData(boolean seal) {
		NbtList list = new NbtList();
		this.pages.stream().map(NbtString::of).forEach(list::add);
		if (!this.pages.isEmpty()) {
			this.itemStack.setSubNbt("pages", list);
		}

		if (seal) {
			this.itemStack.setSubNbt("from", NbtString.of(this.player.getGameProfile().getName()));
//			this.itemStack.setSubNbt("title", NbtString.of(this.title.trim()));
		}
	}

	private void appendNewPage() {
		if (this.countPages() < 100) {
			this.pages.add("");
			this.dirty = true;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (this.sealing) {
//			return this.keyPressedSignMode(keyCode, scanCode, modifiers);
			return false;
		} else {
			boolean valid = this.keyPressedEditMode(keyCode, scanCode, modifiers);
			if (valid) {
				this.invalidatePageContent();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (super.charTyped(chr, modifiers)) {
			return true;
		} else if (this.sealing) {
//			boolean valid = this.bookTitleSelectionManager.insert(chr);
//			if (valid) {
//				this.updateButtons();
//				this.dirty = true;
//				return true;
//			} else {
//				return false;
//			}
			return false;
		} else if (SharedConstants.isValidChar(chr)) {
			this.currentPageSelectionManager.insert(Character.toString(chr));
			this.invalidatePageContent();
			return true;
		} else {
			return false;
		}
	}

	private boolean keyPressedEditMode(int keyCode, int scanCode, int modifiers) {
		if (Screen.isSelectAll(keyCode)) {
			this.currentPageSelectionManager.selectAll();
			return true;
		} else if (Screen.isCopy(keyCode)) {
			this.currentPageSelectionManager.copy();
			return true;
		} else if (Screen.isPaste(keyCode)) {
			this.currentPageSelectionManager.paste();
			return true;
		} else if (Screen.isCut(keyCode)) {
			this.currentPageSelectionManager.cut();
			return true;
		} else {
			SelectionManager.SelectionType selectionType = Screen.hasControlDown() ? SelectionManager.SelectionType.WORD : SelectionManager.SelectionType.CHARACTER;
			switch(keyCode) {
				case 257:
				case 335:
					this.currentPageSelectionManager.insert("\n");
					return true;
				case 259:
					this.currentPageSelectionManager.delete(-1, selectionType);
					return true;
				case 261:
					this.currentPageSelectionManager.delete(1, selectionType);
					return true;
				case 262:
					this.currentPageSelectionManager.moveCursor(1, Screen.hasShiftDown(), selectionType);
					return true;
				case 263:
					this.currentPageSelectionManager.moveCursor(-1, Screen.hasShiftDown(), selectionType);
					return true;
				case 264:
					this.moveDownLine();
					return true;
				case 265:
					this.moveUpLine();
					return true;
				case 266:
					this.previousPageButton.onPress();
					return true;
				case 267:
					this.nextPageButton.onPress();
					return true;
				case 268:
					this.moveToLineStart();
					return true;
				case 269:
					this.moveToLineEnd();
					return true;
				default:
					return false;
			}
		}
	}

	private void moveUpLine() {
		this.moveVertically(-1);
	}

	private void moveDownLine() {
		this.moveVertically(1);
	}

	private void moveVertically(int lines) {
		int sel = this.currentPageSelectionManager.getSelectionStart();
		int line = this.getPageContent().getVerticalOffset(sel, lines);
		this.currentPageSelectionManager.moveCursorTo(line, Screen.hasShiftDown());
	}

	private void moveToLineStart() {
		if (Screen.hasControlDown()) {
			this.currentPageSelectionManager.moveCursorToStart(Screen.hasShiftDown());
		} else {
			int sel = this.currentPageSelectionManager.getSelectionStart();
			int start = this.getPageContent().getLineStart(sel);
			this.currentPageSelectionManager.moveCursorTo(start, Screen.hasShiftDown());
		}
	}

	private void moveToLineEnd() {
		if (Screen.hasControlDown()) {
			this.currentPageSelectionManager.moveCursorToEnd(Screen.hasShiftDown());
		} else {
			PageContent pageContent = this.getPageContent();
			int sel = this.currentPageSelectionManager.getSelectionStart();
			int end = pageContent.getLineEnd(sel);
			this.currentPageSelectionManager.moveCursorTo(end, Screen.hasShiftDown());
		}
	}

//	private boolean keyPressedSignMode(int keyCode, int scanCode, int modifiers) {
//		switch(keyCode) {
//			case 257:
//			case 335:
//				if (!this.title.isEmpty()) {
//					this.saveLetter(true);
//					this.client.setScreen(null);
//				}
//
//				return true;
//			case 259:
//				this.bookTitleSelectionManager.delete(-1);
//				this.updateButtons();
//				this.dirty = true;
//				return true;
//			default:
//				return false;
//		}
//	}

	private String getCurrentPageContent() {
		return this.currentPage >= 0 && this.currentPage < this.pages.size() ? this.pages.get(this.currentPage) : "";
	}

	private void setPageContent(String newContent) {
		if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
			this.pages.set(this.currentPage, newContent);
			this.dirty = true;
			this.invalidatePageContent();
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		this.setFocused(null);
		int i = (this.width - WIDTH) / 2;
		context.drawTexture(this.stationery.guiTexture(), i, 2, 0, 0, WIDTH, HEIGHT);
		if (this.sealing) {
//			boolean blink = this.tickCounter / 6 % 2 == 0;
//			OrderedText cursorText = OrderedText.concat(OrderedText.styledForwardsVisitedString(this.title, Style.EMPTY), blink ? BLACK_CURSOR_TEXT : GRAY_CURSOR_TEXT);
//			int k = this.textRenderer.getWidth(EDIT_TITLE_TEXT);
//			context.drawText(this.textRenderer, EDIT_TITLE_TEXT, i + 36 + (MAX_TEXT_WIDTH - k) / 2, 34, 0x000000, false);
//			int l = this.textRenderer.getWidth(cursorText);
//			context.drawText(this.textRenderer, cursorText, i + 36 + (MAX_TEXT_WIDTH - l) / 2, 50, 0x000000, false);
			int fromWidth = this.textRenderer.getWidth(this.fromText);
			context.drawText(this.textRenderer, this.fromText, i + 36 + (MAX_TEXT_WIDTH - fromWidth) / 2, 60, 0x000000, false);
			context.drawTextWrapped(this.textRenderer, FINALIZE_WARNING_TEXT, i + 36, 82, MAX_TEXT_WIDTH, 0x000000);
		} else {
			int width = this.textRenderer.getWidth(this.pageIndicatorText);
			context.drawText(this.textRenderer, this.pageIndicatorText, i - width + WIDTH - 44, 18, stationery.textColor().getRgb(), false);
			PageContent pageContent = this.getPageContent();

			for(Line line : pageContent.lines) {
				context.drawText(this.textRenderer, line.text, line.x, line.y, stationery.textColor().getRgb(), false);
			}

			this.drawSelection(context, pageContent.selectionRectangles);
			this.drawCursor(context, pageContent.position, pageContent.atEnd);
		}

		super.render(context, mouseX, mouseY, delta);
	}

	private void drawCursor(DrawContext context, Position position, boolean atEnd) {
		if (this.tickCounter / 6 % 2 == 0) {
			position = this.absolutePositionToScreenPosition(position);
			if (!atEnd) {
				context.fill(position.x, position.y - 1, position.x + 1, position.y + 9, stationery.textColor().getRgb());
			} else {
				context.drawText(this.textRenderer, "_", position.x, position.y, stationery.textColor().getRgb(), false);
			}
		}
	}

	private void drawSelection(DrawContext context, Rect2i[] selectionRectangles) {
		for(Rect2i rect2i : selectionRectangles) {
			int minX = rect2i.getX();
			int minY = rect2i.getY();
			int maxX = minX + rect2i.getWidth();
			int maxY = minY + rect2i.getHeight();
			context.fill(RenderLayer.getGuiTextHighlight(), minX, minY, maxX, maxY, 0xFF0000FF);
		}
	}

	private Position screenPositionToAbsolutePosition(Position position) {
		return new Position(position.x - (this.width - WIDTH) / 2 - 36, position.y - 32);
	}

	private Position absolutePositionToScreenPosition(Position position) {
		return new Position(position.x + (this.width - WIDTH) / 2 + 36, position.y + 32);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!super.mouseClicked(mouseX, mouseY, button)) {
			if (button == 0) {
				long millis = Util.getMeasuringTimeMs();
				PageContent pageContent = this.getPageContent();
				int cursorPos = pageContent.getCursorPosition(this.textRenderer, this.screenPositionToAbsolutePosition(new Position((int) mouseX, (int) mouseY)));
				if (cursorPos >= 0) {
					if (cursorPos != this.lastClickIndex || millis - this.lastClickTime >= 250L) {
						this.currentPageSelectionManager.moveCursorTo(cursorPos, Screen.hasShiftDown());
					} else if (!this.currentPageSelectionManager.isSelecting()) {
						this.selectCurrentWord(cursorPos);
					} else {
						this.currentPageSelectionManager.selectAll();
					}

					this.invalidatePageContent();
				}

				this.lastClickIndex = cursorPos;
				this.lastClickTime = millis;
			}

		}
		return true;
	}

	private void selectCurrentWord(int cursor) {
		String content = this.getCurrentPageContent();
		this.currentPageSelectionManager
				.setSelection(TextHandler.moveCursorByWords(content, -1, cursor, false), TextHandler.moveCursorByWords(content, 1, cursor, false));
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (!super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			if (button == 0) {
				PageContent pageContent = this.getPageContent();
				int i = pageContent.getCursorPosition(this.textRenderer, this.screenPositionToAbsolutePosition(new Position((int) mouseX, (int) mouseY)));
				this.currentPageSelectionManager.moveCursorTo(i, true);
				this.invalidatePageContent();
			}

		}
		return true;
	}

	private PageContent getPageContent() {
		if (this.pageContent == null) {
			this.pageContent = this.createPageContent();
			this.pageIndicatorText = Text.translatable("book.pageIndicator", this.currentPage + 1, this.countPages())
					.styled(style -> style.withColor(stationery.textColor()).withFont(stationery.font()));
		}

		return this.pageContent;
	}

	private void invalidatePageContent() {
		this.pageContent = null;
	}

	private void changePage() {
		this.currentPageSelectionManager.putCursorAtEnd();
		this.invalidatePageContent();
	}

	private PageContent createPageContent() {
		String contents = this.getCurrentPageContent();
		if (contents.isEmpty()) {
			return PageContent.EMPTY;
		} else {
			int selStart = this.currentPageSelectionManager.getSelectionStart();
			int selEnd = this.currentPageSelectionManager.getSelectionEnd();
			IntList lineStarts = new IntArrayList();
			List<Line> list = Lists.newArrayList();
			MutableInt lineMut = new MutableInt();
			MutableBoolean mutableBoolean = new MutableBoolean();
			TextHandler textHandler = this.textRenderer.getTextHandler();
			Style stationeryStyle = Style.EMPTY.withColor(this.stationery.textColor()).withFont(this.stationery.font());
			textHandler.wrapLines(contents, MAX_TEXT_WIDTH, stationeryStyle, true, (style, start, end) -> {
				int line = lineMut.getAndIncrement();
				String lineContent = contents.substring(start, end);
				mutableBoolean.setValue(lineContent.endsWith("\n"));
				String strippedContent = StringUtils.stripEnd(lineContent, " \n");
				int lineHeight = line * 9;
				Position linePos = this.absolutePositionToScreenPosition(new Position(0, lineHeight));
				lineStarts.add(start);
				list.add(new Line(style, strippedContent, linePos.x, linePos.y));
			});
			int[] lineStartsArray = lineStarts.toIntArray();
			boolean isAtEnd = selStart == contents.length();
			Position selPos;
			if (isAtEnd && mutableBoolean.isTrue()) {
				selPos = new Position(0, list.size() * 9);
			} else {
				int selLine = getLineFromOffset(lineStartsArray, selStart);
				int selWidth = this.textRenderer.getWidth(Text.literal(contents.substring(lineStartsArray[selLine], selStart)).setStyle(stationeryStyle));
				selPos = new Position(selWidth, selLine * 9);
			}

			List<Rect2i> selections = Lists.newArrayList();
			if (selStart != selEnd) {
				int selMinX = Math.min(selStart, selEnd);
				int selMaxX = Math.max(selStart, selEnd);
				int selMinLine = getLineFromOffset(lineStartsArray, selMinX);
				int selMaxLine = getLineFromOffset(lineStartsArray, selMaxX);
				if (selMinLine == selMaxLine) {
					int lineY = selMinLine * 9;
					int lineStart = lineStartsArray[selMinLine];
					selections.add(this.getLineSelectionRectangle(contents, textHandler, selMinX, selMaxX, lineY, lineStart));
				} else {
					int newEnd = selMinLine + 1 > lineStartsArray.length ? contents.length() : lineStartsArray[selMinLine + 1];
					selections.add(this.getLineSelectionRectangle(contents, textHandler, selMinX, newEnd, selMinLine * 9, lineStartsArray[selMinLine]));

					for(int i = selMinLine + 1; i < selMaxLine; i++) {
						int height = i * 9;
						String lineContents = contents.substring(lineStartsArray[i], lineStartsArray[i + 1]);
						int width = textRenderer.getWidth(Text.literal(lineContents).setStyle(stationeryStyle));
						selections.add(this.getRectFromCorners(new Position(0, height), new Position(width, height + 9)));
					}

					selections.add(this.getLineSelectionRectangle(contents, textHandler, lineStartsArray[selMaxLine], selMaxX, selMaxLine * 9, lineStartsArray[selMaxLine]));
				}
			}

			return new PageContent(
					contents, selPos, isAtEnd, lineStartsArray, list.toArray(new Line[0]), selections.toArray(new Rect2i[0])
			);
		}
	}

	static int getLineFromOffset(int[] lineStarts, int position) {
		int line = Arrays.binarySearch(lineStarts, position);
		return line < 0 ? -(line + 2) : line;
	}

	private Rect2i getLineSelectionRectangle(String string, TextHandler handler, int selectionStart, int selectionEnd, int lineY, int lineStart) {
		String before = string.substring(lineStart, selectionStart);
		String after = string.substring(lineStart, selectionEnd);
		Position posStart = new Position((int)handler.getWidth(before), lineY);
		Position posEnd = new Position((int)handler.getWidth(after), lineY + 9);
		return this.getRectFromCorners(posStart, posEnd);
	}

	private Rect2i getRectFromCorners(Position start, Position end) {
		Position startPos = this.absolutePositionToScreenPosition(start);
		Position endPos = this.absolutePositionToScreenPosition(end);
		int minX = Math.min(startPos.x, endPos.x);
		int maxX = Math.max(startPos.x, endPos.x);
		int minY = Math.min(startPos.y, endPos.y);
		int maxY = Math.max(startPos.y, endPos.y);
		return new Rect2i(minX, minY, maxX - minX, maxY - minY);
	}

	static class Line {
		final Style style;
		final String content;
		final Text text;
		final int x;
		final int y;

		public Line(Style style, String content, int x, int y) {
			this.style = style;
			this.content = content;
			this.x = x;
			this.y = y;
			this.text = Text.literal(content).setStyle(style);
		}
	}

	static class PageContent {
		static final PageContent EMPTY = new PageContent(
				"", new Position(0, 0), true, new int[]{0}, new Line[]{new Line(Style.EMPTY, "", 0, 0)}, new Rect2i[0]
		);
		private final String pageContent;
		final Position position;
		final boolean atEnd;
		private final int[] lineStarts;
		final Line[] lines;
		final Rect2i[] selectionRectangles;

		public PageContent(
				String pageContent, Position position, boolean atEnd, int[] lineStarts, Line[] lines, Rect2i[] selectionRectangles
		) {
			this.pageContent = pageContent;
			this.position = position;
			this.atEnd = atEnd;
			this.lineStarts = lineStarts;
			this.lines = lines;
			this.selectionRectangles = selectionRectangles;
		}

		public int getCursorPosition(TextRenderer renderer, Position position) {
			int lineNum = position.y / 9;
			if (lineNum < 0) {
				return 0;
			} else if (lineNum >= this.lines.length) {
				return this.pageContent.length();
			} else {
				Line line = this.lines[lineNum];
				return this.lineStarts[lineNum] + renderer.getTextHandler().getTrimmedLength(line.content, position.x, line.style);
			}
		}

		public int getVerticalOffset(int position, int lines) {
			int line = getLineFromOffset(this.lineStarts, position);
			int lineNum = line + lines;
			int offset;
			if (0 <= lineNum && lineNum < this.lineStarts.length) {
				int offsetPos = position - this.lineStarts[line];
				int lineLength = this.lines[lineNum].content.length();
				offset = this.lineStarts[lineNum] + Math.min(offsetPos, lineLength);
			} else {
				offset = position;
			}

			return offset;
		}

		public int getLineStart(int position) {
			int line = getLineFromOffset(this.lineStarts, position);
			return this.lineStarts[line];
		}

		public int getLineEnd(int position) {
			int line = getLineFromOffset(this.lineStarts, position);
			return this.lineStarts[line] + this.lines[line].content.length();
		}
	}

	record Position(int x, int y) { }
}
