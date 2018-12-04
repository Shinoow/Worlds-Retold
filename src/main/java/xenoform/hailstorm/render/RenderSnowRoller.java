package xenoform.hailstorm.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xenoform.hailstorm.entity.EntitySnowRoller;
import xenoform.hailstorm.entity.ModelSnowRoller;

import javax.annotation.Nonnull;

public class RenderSnowRoller extends RenderLiving<EntitySnowRoller>
{
    private ResourceLocation TEXTURE = new ResourceLocation("hailstorm:textures/entity/snow_roller.png");
    public static final Factory FACTORY = new Factory();

    public RenderSnowRoller(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelSnowRoller(), 0.5F);
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(@Nonnull EntitySnowRoller entity)
    {
        return TEXTURE;
    }

    @Override
    public void doRender(EntitySnowRoller entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected void preRenderCallback(EntitySnowRoller entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        double s = entitylivingbaseIn.getSize();
        GlStateManager.scale(s, s, s);
        GlStateManager.translate(0.0D, -0.01D, 0.0D);

        if(entitylivingbaseIn.getShrink())
        {
            GlStateManager.scale(1, 1, 1);
        }
    }

    public static class Factory implements IRenderFactory<EntitySnowRoller>
    {

        @Override
        public Render<? super EntitySnowRoller> createRenderFor(RenderManager manager) {
            return new RenderSnowRoller(manager);
        }

    }

}
