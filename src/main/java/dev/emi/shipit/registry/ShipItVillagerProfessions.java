package dev.emi.shipit.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

public class ShipItVillagerProfessions {
	public static final VillagerProfession MAILROOM = new VillagerProfession(
			"shipit:mailroom",
			entry -> entry.matchesKey(ShipItPOIs.MAILROOM_KEY),
			entry -> entry.matchesKey(ShipItPOIs.MAILROOM_KEY),
			ImmutableSet.of(),
			ImmutableSet.of(),
			SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
	
	public static void init() {
		Registry.register(Registries.VILLAGER_PROFESSION, new Identifier("shipit", "mailroom"), MAILROOM);
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.put(MAILROOM, new Int2ObjectOpenHashMap<>(ImmutableMap.of(
				1, new TradeOffers.Factory[]{
						new BuyFactory(Items.PAPER, 24, 1, 16, 2),
						new SellFactory(1, Items.BOOK, 1, 12, 3) // Replace with envelopes or letters when we have them
				},
				2, new TradeOffers.Factory[]{
						new SellFactory(2, ShipItBlocks.PACKAGE, 1, 8, 15),
						new BuyFactory(Items.SLIME_BALL, 18, 1, 12, 10)
				},
				3, new TradeOffers.Factory[]{
						new SellFactory(1, ShipItItems.STAMP, 1, 12, 10),
						new BuyFactory(ShipItItems.BUNDLE_OF_CARDBOARD, 1, 1, 8, 20)
				},
				4, new TradeOffers.Factory[]{
						new SellFactory(1, Items.BOOK, 1, 12, 30),// Replace with fancy colored envelopes or letters when we have them
						new SellFactory(1, Items.BOOK, 1, 12, 30) // Replace with fancy colored envelopes or letters when we have them
				},
				5, new TradeOffers.Factory[]{
						new SellFactory(3, ShipItItems.ADDRESS_PLACARD, 1, 8, 150)
				}
		)));
	}

	static class TradeFactory implements TradeOffers.Factory {
		private final ItemConvertible buyItem, sellItem;
		private final int buyCount, sellCount, maxUses, experience;
		private final float multiplier;

		public TradeFactory(ItemConvertible buyItem, int buyCount, ItemConvertible sellItem, int sellCount, int maxUses, int experience, float multiplier) {
			this.buyItem = buyItem;
			this.sellItem = sellItem;
			this.buyCount = buyCount;
			this.sellCount = sellCount;
			this.maxUses = maxUses;
			this.experience = experience;
			this.multiplier = multiplier;
		}

		@Override
		public TradeOffer create(Entity entity, Random random) {
			return new TradeOffer(new ItemStack(this.buyItem, this.buyCount), new ItemStack(this.sellItem, this.sellCount), this.maxUses, this.experience, multiplier);
		}
	}

	static class BuyFactory extends TradeFactory {

		public BuyFactory(ItemConvertible buyItem, int buyCount, int sellCount, int maxUses, int experience) {
			super(buyItem, buyCount, Items.EMERALD, sellCount, maxUses, experience, 0.05F);
		}
	}

	static class SellFactory extends TradeFactory {

		public SellFactory(int buyCount, ItemConvertible sellItem, int sellCount, int maxUses, int experience) {
			super(Items.EMERALD, buyCount, sellItem, sellCount, maxUses, experience, 0.05F);
		}
	}
}
