package dev.emi.shipit.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.emi.shipit.ShipItMain;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public class StationeryManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
	public static final StationeryManager INSTANCE = new StationeryManager();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final Map<Identifier, Stationery> stationery = new HashMap<>();

	public StationeryManager() {
		super(GSON, "shipit/stationery");
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		stationery.clear();
		for (Identifier id : prepared.keySet()) {
			stationery.put(id, Stationery.CODEC.parse(JsonOps.INSTANCE, prepared.get(id)).getOrThrow(false, ShipItMain.LOGGER::info));
		}
	}

	public Stationery getStationery(Identifier id) {
		return stationery.get(id);
	}

	public void writePacket(PacketByteBuf buf) {
		buf.writeVarInt(stationery.size());
		for (Identifier id : stationery.keySet()) {
			Stationery stationery = this.stationery.get(id);
			buf.writeIdentifier(id);
			buf.writeIdentifier(stationery.itemTexture());
			buf.writeIdentifier(stationery.guiTexture());
			buf.writeIdentifier(stationery.font());
			buf.writeString(stationery.textColor().toString());
		}
	}

	public void readPacket(PacketByteBuf buf) {
		stationery.clear();
		int count = buf.readVarInt();
		for (int i = 0; i < count; i++) {
			stationery.put(buf.readIdentifier(), new Stationery(
					buf.readIdentifier(), //itemTexture
					buf.readIdentifier(), //guiTexture
					buf.readIdentifier(), //font
					TextColor.parse(buf.readString())
			));
		}
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("shipit", "stationery");
	}
}
