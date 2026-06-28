package cn.mc609.ironharvest.fix.mixin.create.fluidFilling;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FluidManipulationBehaviour.class)
public interface FluidManipulationBehaviourAccessor {
    @Accessor("revalidateIn")
    int getRevalidateIn();

    @Accessor("revalidateIn")
    void setRevalidateIn(int value);

    @Accessor("rootPos")
    BlockPos getRootPos();

    @Invoker("setValidationTimer")
    int invokeSetValidationTimer();

    @Invoker("fillInfinite")
    boolean invokeFillInfinite();

    @Invoker("maxBlocks")
    int invokeMaxBlocks();
}
