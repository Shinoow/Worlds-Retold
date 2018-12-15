package xenoform.hailstorm.entity.guardsman;

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
import net.minecraft.world.World;
import xenoform.hailstorm.entity.EntitySurfaceMob;
import xenoform.hailstorm.entity.ISnowCreature;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EntityIceGuardsman extends EntitySurfaceMob implements ISnowCreature
{

    protected static final DataParameter<Boolean> SPINNING = EntityDataManager.createKey(EntityIceGuardsman.class,
            DataSerializers.BOOLEAN);

    private Random rand = new Random();
    private int ticksSinceLastAttack = 0;

    public EntityIceGuardsman(World world){
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
    }

    public boolean getSpinning(){
        return this.dataManager.get(SPINNING);
    }

    public void setSpinning(boolean spin){
        this.dataManager.set(SPINNING, spin);
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

        if(getAttackTarget() == null)
        {
            List<EntityPlayer> list = this.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(32.0D, 32.0D, 32.0D));
            for(EntityPlayer entity : list)
            {
                if(entity!=null)
                    setAttackTarget(entity);
            }
        }

        boolean minHeight = world.getBlockState(getPosition().down(3)) == Blocks.AIR.getDefaultState();

        if(!minHeight)
            motionY += .1;
        else
            motionY = 0;

     //   System.out.println(getAttackTarget());

        ticksSinceLastAttack++;
        boolean thirtyPercent = rand.nextInt(3) == 0;

        try{
            if(!world.isRemote && getAttackTarget() != null) {
                if (ticksSinceLastAttack == 20 && thirtyPercent) {
                    System.out.println("one second");
                    chargeAttack();
                    ticksSinceLastAttack = 0;
                } else if (ticksSinceLastAttack == 40 && thirtyPercent) {
                    System.out.println("two seconds");
                    chargeAttack();
                    ticksSinceLastAttack = 0;
                } else if (ticksSinceLastAttack == 60 && thirtyPercent) {
                    System.out.println("three seconds");
                    chargeAttack();
                    ticksSinceLastAttack = 0;
                } else if (ticksSinceLastAttack == 80) {
                    System.out.println("four seconds");
                    chargeAttack();
                    ticksSinceLastAttack = 0;
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void chargeAttack() throws InterruptedException{
        setSpinning(false);
        System.out.println("prior to wait");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("fire");
        shootFreezingProjectile(getAttackTarget());
        setSpinning(true);
    }

    private void shootFreezingProjectile(EntityLivingBase p_82216_2_)
    {
        this.launchWitherSkullToCoords(p_82216_2_.posX, p_82216_2_.posY + (double)p_82216_2_.getEyeHeight() * 0.5D, p_82216_2_.posZ);
    }

    /**
     * Launches a Wither skull toward (par2, par4, par6)
     */
    private void launchWitherSkullToCoords(double x, double y, double z)
    {
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
		else if(getSpinning())
            return;
		else
			super.damageEntity(damageSrc, damageAmount);
	}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Spinning", this.getSpinning());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setSpinning(compound.getBoolean("Spinning"));
    }

}
