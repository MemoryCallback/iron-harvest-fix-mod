package cn.mc609.ironharvest.fix.mixin.create.fluidFilling;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FluidManipulationBehaviour.class)
public interface FluidManipulationBehaviourInvoker {
    @Invoker("setValidationTimer")
    int invokeSetValidationTimer();

    @Invoker("fillInfinite")
    boolean invokeFillInfinite();
}
