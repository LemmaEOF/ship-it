package dev.emi.shipit.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.shipit.registry.ShipItComponents;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	
	@Inject(at = @At("TAIL"), method = "onPlayerConnect")
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		// Keep track of a player existing, even if they don't have a mailbox
		ShipItComponents.MAIL.get(player.getServerWorld().getLevelProperties()).getMailInfo(player).tryNameUpdate(player);
	}
}
