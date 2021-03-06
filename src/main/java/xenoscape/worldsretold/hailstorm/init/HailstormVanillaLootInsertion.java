package xenoscape.worldsretold.hailstorm.init;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HailstormVanillaLootInsertion {

	@SubscribeEvent
	public void lootLoad(LootTableLoadEvent e)
	{
		if (e.getName().toString().equals("minecraft:chests/igloo_chest"))
		{
			LootEntry entry = new LootEntryItem(HailstormItems.CRYONITE, 6, 2, new LootFunction[0], new LootCondition[0],
					"worldsretold:cryonite_igloo");
			LootPool pool1 = new LootPool(new LootEntry[] { entry }, new LootCondition[0], new RandomValueRange(2, 8),
					new RandomValueRange(0, 0), "worldsretold_pool_inject");
			e.getTable().addPool(pool1);
		}
	}
}
