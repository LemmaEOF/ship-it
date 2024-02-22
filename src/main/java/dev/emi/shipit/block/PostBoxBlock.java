package dev.emi.shipit.block;

import dev.emi.shipit.registry.ShipItComponents;
import dev.emi.shipit.screen.handler.PostBoxScreenHandler;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.level.LevelComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PostBoxBlock extends Block {
	private static final Text TITLE = Text.translatable("container.shipit.post_box");

	public PostBoxBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			LevelComponents.sync(ShipItComponents.MAIL, ((ServerWorld) world).getServer(), (AutoSyncedComponent) ShipItComponents.MAIL.get(world.getLevelProperties()), p -> p == player);
			player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> new PostBoxScreenHandler(i, playerInventory), TITLE));
		}
		return ActionResult.SUCCESS;
	}
}
