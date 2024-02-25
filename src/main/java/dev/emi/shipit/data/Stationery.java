package dev.emi.shipit.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

//TODO: automatically determine item model/gui texture IDs? That'd make loading them way easier
public record Stationery(Identifier itemModel, Identifier guiTexture, Identifier font, TextColor textColor) {
	public static final Codec<Stationery> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("item_model").forGetter(Stationery::itemModel),
					Identifier.CODEC.fieldOf("gui_texture").forGetter(Stationery::guiTexture),
					Identifier.CODEC.fieldOf("font").forGetter(Stationery::font),
					TextColor.CODEC.fieldOf("text_color").forGetter(Stationery::textColor)
			).apply(instance, Stationery::new)
	);
}
