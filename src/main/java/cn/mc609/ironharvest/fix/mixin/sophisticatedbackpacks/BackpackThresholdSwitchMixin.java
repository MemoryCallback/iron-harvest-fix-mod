package cn.mc609.ironharvest.fix.mixin.sophisticatedbackpacks;

import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 向 BackpackBlockEntity 注入 ThresholdSwitchObservable 接口，
 * 使 Create 存量转信器（阈值开关）能直接通过该接口读取精妙背包的库存量。
 *
 * 存量转信器在 updateCurrentLevel() 中优先检查 instanceof
 * ThresholdSwitchObservable，若匹配则直接读数，绕开 Capability 系统路径。
 */
@Mixin(value = BackpackBlockEntity.class, remap = false)
@Implements(@Interface(iface = ThresholdSwitchObservable.class, prefix = "sbfix$"))
public abstract class BackpackThresholdSwitchMixin {

    @Shadow
    public abstract IBackpackWrapper getBackpackWrapper();

    public int sbfix$getMaxValue() {
        IItemHandler handler = getBackpackWrapper().getInventoryForInputOutput();
        if (handler == null)
            return 0;
        int max = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            max += handler.getSlotLimit(i);
        }
        return max;
    }

    public int sbfix$getMinValue() {
        return 0;
    }

    public int sbfix$getCurrentValue() {
        IItemHandler handler = getBackpackWrapper().getInventoryForInputOutput();
        if (handler == null)
            return 0;
        int count = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            count += handler.getStackInSlot(i).getCount();
        }
        return count;
    }

    public MutableComponent sbfix$format(int value) {
        return Component.literal(String.valueOf(value));
    }
}
