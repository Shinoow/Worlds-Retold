package xenoscape.worldsretold.hailstorm.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import xenoscape.worldsretold.defaultmod.WorldsRetoldTabs;
import xenoscape.worldsretold.defaultmod.basic.BasicItem;
import xenoscape.worldsretold.hailstorm.entity.projectiles.egg.EntityPenguinEgg;

public class BasicItemPenguinEgg extends BasicItem {
	protected String name;

	public BasicItemPenguinEgg(String name) {
		super(name);
		this.maxStackSize = 16;
		this.setCreativeTab(WorldsRetoldTabs.W_TAB);
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if (!playerIn.capabilities.isCreativeMode) {
			itemstack.shrink(1);
		}

		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
				SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!worldIn.isRemote) {
			EntityPenguinEgg entityegg = new EntityPenguinEgg(worldIn, playerIn);
			entityegg.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entityegg);
		}

		playerIn.addStat(StatList.getObjectUseStats(this));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
}