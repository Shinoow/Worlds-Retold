package xenoform.hailstorm.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import xenoform.hailstorm.entity.automaton.EntityAutomaton;

import java.util.List;

public abstract class EntitySurfaceGuard extends EntitySurfaceMob
{
    protected static final DataParameter<Boolean> ACTIVE = EntityDataManager.createKey(EntityAutomaton.class,
            DataSerializers.BOOLEAN);
    protected float targetDistance;

    public EntitySurfaceGuard(World world){
        super(world);
    }

    public boolean isActive() {
        return getDataManager().get(ACTIVE);
    }

    public void setActive(boolean isActive) {
        getDataManager().set(ACTIVE, isActive);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ACTIVE, false);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (getAttackTarget() == null) {
            List<EntityPlayer> list = this.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class,
                    this.getEntityBoundingBox().expand(32.0D, 32.0D, 32.0D));
            for (EntityPlayer entity : list) {
                if (entity != null && !world.isRemote && !entity.isCreative()) {
                    setAttackTarget(entity);
                }
            }
        }

        if (getAttackTarget() != null)
            targetDistance = getDistance(getAttackTarget());

        if (!world.isRemote) {
            if (!isAIDisabled()) {
                if (isActive()) {
                    if (getAttackTarget() == null && moveForward == 0) {
                        setActive(false);
                    }
                } else if (getAttackTarget() != null && targetDistance <= 5) {
                    setActive(true);
                }
            }
        }

        if (isActive() && targetDistance > 16 && !world.isRemote)
            setActive(false);

        if (!isActive()) {
            posX = prevPosX;
            posZ = prevPosZ;
            rotationYaw = prevRotationYaw;
        }

        if (!world.isRemote) {
            // System.out.println(getAttackTarget());
            // System.out.println(isActive());
            // System.out.println(targetDistance);
        }
        if (getAttackTarget() != null && isActive()) {
            getNavigator().tryMoveToEntityLiving(getAttackTarget(), .2D);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Active", isActive());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setActive(compound.getBoolean("Active"));
    }

    @Override
    public boolean canBePushed() {
        return isActive();
    }

    @Override
    public boolean canBeCollidedWith() {
        return super.canBeCollidedWith() && isActive();
    }

    public boolean attackable() {
        return isActive();
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }

    public void travel(float strafe, float vertical, float forward) {
        super.travel(strafe, vertical, forward);
    }


}
