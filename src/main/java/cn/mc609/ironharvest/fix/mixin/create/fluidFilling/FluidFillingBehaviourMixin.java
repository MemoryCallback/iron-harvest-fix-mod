package cn.mc609.ironharvest.fix.mixin.create.fluidFilling;

import java.util.List;
import java.util.Set;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;

/**
 * 修复 fillInfinite=true 时软管滑轮填充到一定量后
 * 不放置源块、不消耗流体的死锁问题。
 *
 * 根因：continueValidation() 中 infinite=true 时调用了 reset()，
 * 清空了 tryDeposit() BFS 用的 queue，导致后续填充请求跳过 BFS 直接返回 false。
 *
 * 修复：fillInfinite=true 时只清理验证状态，不动 BFS 队列。
 */
@Mixin(value = FluidFillingBehaviour.class, remap = false)
public abstract class FluidFillingBehaviourMixin {

    @Shadow
    private List<FluidManipulationBehaviour.BlockPosEntry> infinityCheckFrontier;

    @Shadow
    private Set<BlockPos> infinityCheckVisited;

    /**
     * 将 continueValidation() 中 infinite=true 分支的 reset()
     * 替换为轻量清理——当 fillInfinite=true 时，
     * 只清理质检员自己的验证状态，不动 BFS 队列。
     */
    @Redirect(
            method = "continueValidation",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/fluids/transfer/FluidFillingBehaviour;reset()V",
                    ordinal = 1
            ),
            require = 1
    )
    private void onInfiniteReset(FluidFillingBehaviour self) {
        if (((FluidManipulationBehaviourInvoker)this).invokeFillInfinite()) {
            this.infinityCheckFrontier.clear();
            this.infinityCheckVisited.clear();
            ((FluidManipulationBehaviourInvoker)this).invokeSetValidationTimer();
        } else {
            self.reset();
        }
    }
}