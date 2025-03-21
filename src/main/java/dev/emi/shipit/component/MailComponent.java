package dev.emi.shipit.component;

import java.util.Map;
import java.util.UUID;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface MailComponent extends ComponentV3 {

	PlayerMailInfo getMailInfo(PlayerEntity player);
	
	Map<UUID, PlayerMailInfo> getAllMailInfos();

	
	boolean sendMail(UUID uuid, ItemStack stack);
}
