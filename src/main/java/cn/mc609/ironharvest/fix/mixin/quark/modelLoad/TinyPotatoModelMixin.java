package cn.mc609.ironharvest.fix.mixin.quark.modelLoad;

import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 修复与 NeoContinuity 的冲突，在加载资源时出错而无法应用部分连接纹理
// by DawnString
@Mixin(targets = "org.violetmoon.quark.addons.oddities.client.model.TinyPotatoModel")
public abstract class TinyPotatoModelMixin {
    @Shadow
    private BakedModel originalModel;

    @Inject(method = "isCustomRenderer", at = @At("HEAD"), cancellable = true)
    private void neocontinuityfixed$handleNullOriginalModel(CallbackInfoReturnable<Boolean> cir) {
        if (this.originalModel == null) {
            cir.setReturnValue(false);
        }
    }
}
