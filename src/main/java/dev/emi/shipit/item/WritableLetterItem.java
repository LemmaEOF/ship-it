package dev.emi.shipit.item;

import dev.emi.shipit.screen.LetterEditScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WritableLetterItem extends Item {
	public WritableLetterItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (world.isClient) {
			MinecraftClient.getInstance().setScreen(new LetterEditScreen(user, stack, hand));
		}
		return TypedActionResult.success(stack, world.isClient());
	}

	public static boolean isValid(@Nullable NbtCompound nbt) {
		if (nbt == null) {
			return false;
		} else if (!nbt.contains("pages", NbtElement.LIST_TYPE)) {
			return false;
		} else {
			NbtList nbtList = nbt.getList("pages", NbtElement.STRING_TYPE);

			for(int i = 0; i < nbtList.size(); ++i) {
				String string = nbtList.getString(i);
				if (string.length() > 32767) {
					return false;
				}
			}

			return true;
		}
	}
}
