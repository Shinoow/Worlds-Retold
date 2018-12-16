package xenoform.hailstorm.entity.guardsman;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import xenoform.hailstorm.entity.EntitySurfaceMonster;
import xenoform.hailstorm.entity.ISnowCreature;

import java.util.List;
import java.util.Random;

public class EntityGuardsman extends EntitySurfaceMonster implements ISnowCreature {

	protected static final DataParameter<Boolean> SPINNING = EntityDataManager.createKey(EntityGuardsman.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Integer> TICKS_SINCE_LAST_ATTACK = EntityDataManager
			.createKey(EntityGuardsman.class, DataSerializers.VARINT);
	protected static final DataParameter<Boolean> CHARGING = EntityDataManager.createKey(EntityGuardsman.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Integer> CHARGE_TICKS = EntityDataManager.createKey(EntityGuardsman.class,
			DataSerializers.VARINT);

	private Random rand = new Random();
	private int ticksSinceLastAttack = 0;
	private int chargeTicks = 0;

	public EntityGuardsman(World world) {
		super(world);
		setSize(2, 2);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAIWander(this, .8D));
		this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 64));
		this.tasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(SPINNING, true);
		this.dataManager.register(TICKS_SINCE_LAST_ATTACK, 0);
		this.dataManager.register(CHARGING, false);
		this.dataManager.register(CHARGE_TICKS, 0);
	}

	public boolean getSpinning() {
		return this.dataManager.get(SPINNING);
	}

	public void setSpinning(boolean spin) {
		this.dataManager.set(SPINNING, spin);
	}

	public int getTicksSinceLastAttack() {
		return this.dataManager.get(TICKS_SINCE_LAST_ATTACK);
	}

	public void setTicksSinceLastAttack(int ticks) {
		this.dataManager.set(TICKS_SINCE_LAST_ATTACK, ticks);
	}

	public boolean getCharging() {
		return this.dataManager.get(CHARGING);
	}

	public void setCharging(boolean spin) {
		this.dataManager.set(CHARGING, spin);
	}

	public int getChargeTicks() {
		return this.dataManager.get(CHARGE_TICKS);
	}

	public void setChargeTicks(int ticks) {
		this.dataManager.set(CHARGE_TICKS, ticks);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(.3D);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (getAttackTarget() == null) {
			List<EntityPlayer> list = this.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class,
					this.getEntityBoundingBox().expand(22.0D, 22.0D, 22.0D));
			for (EntityPlayer entity : list) {
				if (entity != null)
					setAttackTarget(entity);
			}
		}

		boolean minHeight = world.getBlockState(getPosition().down(2)) == Blocks.AIR.getDefaultState();

		if (!minHeight)
			motionY += .1;
		else
			motionY = 0;

		if (!world.isRemote) {
			if (getCharging() && chargeTicks < 40) {
				chargeTicks++;
				setChargeTicks(chargeTicks);
			}

			if (chargeTicks >= 40 && !getCharging()) {
				setChargeTicks(0);
				chargeTicks = 0;
			}
		}

		ticksSinceLastAttack++;
		setTicksSinceLastAttack(ticksSinceLastAttack);

		// System.out.println("last attack:" + ticksSinceLastAttack);
		// System.out.println("charge:" + chargeTicks);

		// System.out.println(ticksSinceLastAttack);
		// System.out.println(getChargeTicks());
		// System.out.println(getCharging());

		boolean thirtyPercent = rand.nextInt(3) == 0;

		if (!world.isRemote && getAttackTarget() != null) {
			if (getTicksSinceLastAttack() == 20 && thirtyPercent) {
				System.out.println(this + "one second");
				setCharging(true);
				chargeAttack();
				setTicksSinceLastAttack(0);
				ticksSinceLastAttack = 0;
			} else if (getTicksSinceLastAttack() == 40 && thirtyPercent) {
				System.out.println(this + "two seconds");
				setCharging(true);
				chargeAttack();
				setTicksSinceLastAttack(0);
				ticksSinceLastAttack = 0;
			} else if (getTicksSinceLastAttack() == 60 && thirtyPercent) {
				System.out.println(this + "three seconds");
				setCharging(true);
				chargeAttack();
				setTicksSinceLastAttack(0);
				ticksSinceLastAttack = 0;
			} else if (getTicksSinceLastAttack() >= 80) {
				System.out.println(this + "four seconds");
				setCharging(true);
				chargeAttack();
				setTicksSinceLastAttack(0);
				ticksSinceLastAttack = 0;
			}
		}
		if (!this.world.isRemote && this.getAttackTarget() != null) {
			Entity entity = this.getAttackTarget();
			
			this.getLookHelper().setLookPositionWithEntity((Entity) getAttackTarget(), 10.0f,
					(float) this.getVerticalFaceSpeed());

			if (entity != null) {
				double d0 = entity.posX - this.posX;
				double d1 = entity.posZ - this.posZ;
				double d3 = d0 * d0 + d1 * d1;

				if (d3 > 9.0D) {
					double d5 = (double) MathHelper.sqrt(d3);
					this.motionX += (d0 / d5 * 0.25D - this.motionX) * 0.1000000238418579D;
					this.motionZ += (d1 / d5 * 0.25D - this.motionZ) * 0.1000000238418579D;
				}
			}
		}

		this.rotationYaw = (float) MathHelper.atan2(this.motionZ, this.motionX) * (180F / (float) Math.PI) - 90.0F;
	}

	private void chargeAttack() {
		System.out.println(this + "called");
		setSpinning(false);
		System.out.println(getChargeTicks());
		if (getChargeTicks() == 40) {
			shootFreezingProjectile(getAttackTarget());
			setSpinning(true);
			setChargeTicks(0);
			setCharging(false);
		}
	}

	private void shootFreezingProjectile(EntityLivingBase p_82216_2_) {
		this.launchWitherSkullToCoords(p_82216_2_.posX, p_82216_2_.posY + (double) p_82216_2_.getEyeHeight() * 0.5D,
				p_82216_2_.posZ);
	}

	/**
	 * Launches a Wither skull toward (par2, par4, par6)
	 */
	private void launchWitherSkullToCoords(double x, double y, double z) {
		double d0 = this.posX;
		double d1 = this.posY;
		double d2 = this.posZ;
		double d3 = x - d0;
		double d4 = y - d1;
		double d5 = z - d2;
		EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.world, this, d3, d4, d5);

		entitywitherskull.posY = d1;
		entitywitherskull.posX = d0;
		entitywitherskull.posZ = d2;
		this.world.spawnEntity(entitywitherskull);
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		if (damageSrc == DamageSource.ON_FIRE || damageSrc == DamageSource.HOT_FLOOR)
			super.damageEntity(damageSrc, damageAmount * 2);
		else if (damageSrc == DamageSource.LAVA)
			super.damageEntity(damageSrc, damageAmount * 3);
		else if (getSpinning())
			return;
		else
			super.damageEntity(damageSrc, damageAmount);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("Spinning", this.getSpinning());
		compound.setInteger("Ticks", this.getTicksSinceLastAttack());
		compound.setBoolean("Charging", this.getCharging());
		compound.setInteger("ChargeTicks", this.getChargeTicks());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setSpinning(compound.getBoolean("Spinning"));
		this.setTicksSinceLastAttack(compound.getInteger("Ticks"));
		this.setCharging(compound.getBoolean("Charging"));
		this.setChargeTicks(compound.getInteger("ChargeTicks"));
	}
}