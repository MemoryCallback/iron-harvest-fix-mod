package cn.mc609.ironharvest.fix.mixin.create.fluidFilling;

import java.util.List;
import java.util.Set;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;

/**
 * 修复 fillInfinite=true 时软管滑轮填充到一定量后不放置源块、不消耗流体的死锁问题。
 *
 * continueValidation 中 frontier 搜索完成且 infinite=true 时会调用 reset()，
 * 该操作会清空主 BFS 队列（queue）、visited、infinityCheck* 结构，
 * 并将 infinite 置为 false、affectedArea 置为 null。
 *
 * 修复：在 reset() 调用前拦截。当 fillInfinite=true 时跳过完整的 reset()，
 * 改为轻量清理（清空 infinityCheckVisited、重新播种 frontier、重置验证计时器），
 * 保留主 BFS visited/queue 状态不被破坏。
 */
@Mixin(value = FluidFillingBehaviour.class, remap = false)
public abstract class FluidFillingBehaviourMixin {

    @Shadow
    private List<FluidManipulationBehaviour.BlockPosEntry> infinityCheckFrontier;

    @Shadow
    private Set<BlockPos> infinityCheckVisited;

    @Inject(
            method = "continueValidation",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/fluids/transfer/FluidFillingBehaviour;" +
                            "reset()V",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void onInfiniteReset(CallbackInfo ci) {
        FluidManipulationBehaviourAccessor acc =
                (FluidManipulationBehaviourAccessor) this;
        if (acc.invokeFillInfinite()) {
            this.infinityCheckFrontier.clear();
            this.infinityCheckVisited.clear();
            this.infinityCheckFrontier.add(
                    new FluidManipulationBehaviour.BlockPosEntry(acc.getRootPos(), 0));
            acc.invokeSetValidationTimer();
            ci.cancel();
        }
    }
}
