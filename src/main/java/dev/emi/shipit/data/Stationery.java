package dev.emi.shipit.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

public record Stationery(Identifier itemTexture, Identifier guiTexture, Identifier font, TextColor textColor) {
	public static final Codec<Stationery> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("item_texture").forGetter(Stationery::itemTexture),
					Identifier.CODEC.fieldOf("gui_texture").forGetter(Stationery::guiTexture),
					Identifier.CODEC.fieldOf("font").forGetter(Stationery::font),
					TextColor.CODEC.fieldOf("text_color").forGetter(Stationery::textColor)
			).apply(instance, Stationery::new)
	);
}
