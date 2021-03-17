package dev.emi.shipit.block;

import dev.emi.shipit.registry.ShipItComponents;
import dev.emi.shipit.registry.ShipItItems;
import dev.emi.shipit.screen.handler.MailBoxScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MailBoxBlock extends Block {
	private static final TranslatableText TITLE = new TranslatableText("container.shipit.mail_box");

	public MailBoxBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onPlaced(world, pos, state, placer, stack);
		if (placer instanceof PlayerEntity) {
			ShipItComponents.MAIL.get(world.getLevelProperties()).getMailInfo((PlayerEntity) placer).placed = true;
		}
		/*BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof MailBoxBlockEntity) {
			((MailBoxBlockEntity) be).setOwner(placer);
		}*/
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() == ShipItItems.ADDRESS_PLACARD && stack.hasCustomName()) {
			ShipItComponents.MAIL.get(world.getLevelProperties()).getMailInfo(player).address = stack.getName().asString();
			if (!player.isCreative()) {
				stack.decrement(1);
			}
		} else {
			if (!world.isClient) {
				player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
					return new MailBoxScreenHandler(i, playerInventory, ShipItComponents.MAIL.get(world.getLevelProperties()).getMailInfo(player));
				}, TITLE));
			}
		}
		return ActionResult.SUCCESS;
	}

	/*
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new MailBoxBlockEntity();
	}*/
}
