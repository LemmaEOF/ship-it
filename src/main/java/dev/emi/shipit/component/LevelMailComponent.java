package dev.emi.shipit.component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.WorldProperties;

public class LevelMailComponent implements MailComponent, AutoSyncedComponent {
	private Map<UUID, PlayerMailInfo> MAIL_INFOS = new HashMap<>();

	public LevelMailComponent(WorldProperties properties) {
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		MAIL_INFOS.clear();
		NbtList infos = tag.getList("Infos", 10);
		for (NbtElement i : infos) {
			PlayerMailInfo info = new PlayerMailInfo();
			info.fromTag((NbtCompound) i);
			MAIL_INFOS.put(info.uuid, info);
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtList infos = new NbtList();
		for (Map.Entry<UUID, PlayerMailInfo> entry : MAIL_INFOS.entrySet()) {
			infos.add(entry.getValue().toTag());
		}
		tag.put("Infos", infos);
	}

	@Override
	public PlayerMailInfo getMailInfo(PlayerEntity player) {
		UUID uuid = player.getUuid();
		if (!MAIL_INFOS.containsKey(uuid)) {
			MAIL_INFOS.put(uuid, new PlayerMailInfo(player));
		}
		return MAIL_INFOS.get(uuid);
	}

	@Override
	public Map<UUID, PlayerMailInfo> getAllMailInfos() {
		return MAIL_INFOS;
	}

	@Override
	public boolean sendMail(/*MinecraftServer server, */UUID uuid, ItemStack stack) {
		PlayerMailInfo info = MAIL_INFOS.get(uuid);
		for (int i = 0; i < info.size(); i++) {
			if (info.getStack(i).isEmpty()) {
				info.setStack(i, stack);
				return true;
			}
		}
		/*
		if (info.mailBoxDimension != null && info.mailBoxPos != null) {
			World mailWorld = server.getWorld(info.mailBoxDimension);
			if (mailWorld.isChunkLoaded(info.mailBoxPos)) {
				BlockState state = mailWorld.getBlockState(info.mailBoxPos);
				if (state.getBlock() instanceof MailBoxBlock) {
					BlockEntity be = mailWorld.getBlockEntity(info.mailBoxPos);
					if (be instanceof MailBoxBlockEntity) {
						Inventory box = (Inventory) be;
						for (int i = 0; i < box.size(); i++) {
							if (box.getStack(i).isEmpty()) {
								box.setStack(i, stack);
								return true;
							}
						}
					}
				}
			}
		}*/
		return false;
	}
	/*
	@Override
	public void setMailBox(PlayerEntity player, World world, BlockPos pos) {
		PlayerMailInfo info = getMailInfo(player);
		info.mailBoxPos = pos;
		info.mailBoxDimension = world.getRegistryKey();
	}*/

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player) {
		return false;
	}
}
