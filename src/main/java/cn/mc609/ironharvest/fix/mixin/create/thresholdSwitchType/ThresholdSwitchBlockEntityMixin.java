package cn.mc609.ironharvest.fix.mixin.create.thresholdSwitchType;

import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复客户端存量转信器 UI 对精妙背包的显示问题。
 *
 * 不碰 updateCurrentLevel()——检测交由 BackpackThresholdSwitchMixin
 * 的接口注入（Observable 路径）处理。
 */
@Mixin(value = ThresholdSwitchBlockEntity.class, remap = false)
public abstract class ThresholdSwitchBlockEntityMixin {

    @Shadow
    private BlockPos getTargetPos() { return null; }

    /**
     * getTypeOfCurrentTarget() 返回 UNSUPPORTED 时，若目标是精妙背包
     * 则改为 ITEM，让客户端 UI 正确显示库存信息。
     */
    @Inject(method = "getTypeOfCurrentTarget", at = @At("RETURN"), cancellable = true, remap = false)
    private void fixBackpackType(CallbackInfoReturnable<ThresholdSwitchBlockEntity.ThresholdType> cir) {
        if (cir.getReturnValue() == ThresholdSwitchBlockEntity.ThresholdType.UNSUPPORTED) {
            Level level = ((BlockEntity) (Object) this).getLevel();
            if (level != null && level.getBlockEntity(getTargetPos()) instanceof BackpackBlockEntity) {
                cir.setReturnValue(ThresholdSwitchBlockEntity.ThresholdType.ITEM);
            }
        }
    }

    /**
     * getDisplayItemForScreen() 返回空物品时（Block.asItem() 注册表查询
     * 可能不返回对应物品），手动从注册表查询，避免 hover 时显示
     * "未附加到方块"。
     */
    @Inject(method = "getDisplayItemForScreen", at = @At("RETURN"), cancellable = true, remap = false)
    private void fixBackpackDisplayItem(CallbackInfoReturnable<ItemStack> cir) {
        if (!cir.getReturnValue().isEmpty())
            return;

        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null)
            return;

        BlockEntity be = level.getBlockEntity(getTargetPos());
        if (!(be instanceof BackpackBlockEntity))
            return;

        Block block = level.getBlockState(getTargetPos()).getBlock();
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        Item item = BuiltInRegistries.ITEM.get(key);
        if (item != null && item != Items.AIR) {
            cir.setReturnValue(new ItemStack(item));
        }
    }
}
