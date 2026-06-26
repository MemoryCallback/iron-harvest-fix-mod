package cn.mc609.ironharvest.fix.mixin.createStuffAdditions.blockPicker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Block Picker 修复：
 *
 * 1. 禁止抓取多部分方块（门/床/双层植物等），
 *    避免抓取一半后另一半残留。
 * 2. 对正常方块抓取时使用完整更新 flags (19)，
 *    保证邻居方块的 updateShape 被正确触发。
 */
@Mixin(targets = "net.mcreator.createstuffadditions.procedures.BlockPickerRightclickedOnBlockProcedure", remap = false)
public class BlockPickerRightclickedOnBlockProcedureMixin {

    /**
     * 多部分方块的 destroySpeed → -1，
     * 利用 execute() 中已有的 speed < 0 → 跳过抓取 逻辑，
     * 让 LiftedBlockEntity 的生成和 setBlock 都不执行。
     */
    @Redirect(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
        ),
        require = 1
    )
    private static float preventMultiPartPickup(BlockState state, BlockGetter level, BlockPos pos) {
        if (isMultiPartBlock(state)) {
            return -1.0f;
        }
        return state.getDestroySpeed(level, pos);
    }

    /**
     * setBlock flags 3 → 19 (加入 UPDATE_KNOWN_SHAPE)，
     * 保证抓取正常方块时邻居的 updateShape 正确触发。
     */
    @ModifyArg(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
        ),
        index = 2,
        require = 1
    )
    private static int fixBlockUpdateFlags(int flags) {
        return flags == 3 ? 19 : flags;
    }

    /**
     * 判断方块是否为多部分方块。
     * 利用 BlockStateProperties 中已知的多部分属性。
     */
    private static boolean isMultiPartBlock(BlockState state) {
        return state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
            || state.hasProperty(BlockStateProperties.BED_PART);
    }
}
