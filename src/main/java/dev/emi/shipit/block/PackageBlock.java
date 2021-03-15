package dev.emi.shipit.block;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import dev.emi.shipit.block.entity.PackageBlockEntity;
import dev.emi.shipit.registry.ShipItItems;
import dev.emi.shipit.screen.handler.PackageScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PackageBlock extends Block implements BlockEntityProvider {
	private static final TranslatableText TITLE = new TranslatableText("container.shipit.package");
	public static final EnumProperty<PackageState> PACKAGE_STATE = EnumProperty.of("package_state", PackageState.class);
	public static final IntProperty STAMPS = IntProperty.of("stamps", 0, 3);

	public PackageBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(PACKAGE_STATE, PackageState.NEW).with(STAMPS, 0));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(PACKAGE_STATE);
		builder.add(STAMPS);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (state.get(PACKAGE_STATE) == PackageState.NEW) {
			ItemStack stack = player.getStackInHand(hand);
			if (stack.getItem() == ShipItItems.STAMP && state.get(STAMPS) < 3) {
				if (!player.isCreative()) {
					stack.decrement(1);
				}
				world.setBlockState(pos, state.with(STAMPS, state.get(STAMPS) + 1), 18);
				return ActionResult.SUCCESS;
			}
		}
		if (!world.isClient) {
			player.openHandledScreen(new ExtendedScreenHandlerFactory(){

				public Text getDisplayName() {
					return TITLE;
				}

				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new PackageScreenHandler(syncId, inv, state.get(STAMPS), (Inventory) world.getBlockEntity(pos));
				}

				public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
					buf.writeInt(state.get(STAMPS));
				}	
			});
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder) {
		BlockEntity be = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
		if (be instanceof PackageBlockEntity) {
			PackageBlockEntity pbe = (PackageBlockEntity) be;
			if (state.get(PACKAGE_STATE) == PackageState.NEW) {
				ItemStack stack = new ItemStack(this);
				if (!pbe.isEmpty()) {
					stack.putSubTag("Stamps", IntTag.of(state.get(STAMPS)));
					stack.putSubTag("PackageState", IntTag.of(1));
					stack.putSubTag("BlockEntityTag", pbe.getInventoryTag(new CompoundTag()));
				}
				return Lists.newArrayList(stack);
			} else {
				List<ItemStack> list = new ArrayList<>();
				list.add(new ItemStack(ShipItItems.BUNDLE_OF_CARDBOARD));
				for (int i = 0; i < pbe.size(); i++) {
					ItemStack stack = pbe.getStack(i);
					if (!stack.isEmpty()) {
						list.add(stack);
					}
				}
				return list;
			}
		}
		return super.getDroppedStacks(state, builder);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		CompoundTag tag = ctx.getStack().getTag();
		if (tag != null && tag.contains("PackageState")) {
			int stamps = 0;
			if (tag.contains("Stamps")) {
				stamps = tag.getInt("Stamps");
			}
			switch (tag.getInt("PackageState")) {
				case 1:
					return this.getDefaultState().with(PACKAGE_STATE, PackageState.OPENED).with(STAMPS, stamps);
			}
		}
		return super.getPlacementState(ctx);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new PackageBlockEntity();
	}

	public static enum PackageState implements StringIdentifiable {
		NEW("new"),
		OPENED("opened");

		private final String name;

		private PackageState(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}
	}
}
