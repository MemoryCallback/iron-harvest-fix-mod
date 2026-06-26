# IronHarvestFix

修复针对 Minecraft 1.21.1 NeoForge 整合包 **IronHarvest** 中由 Mod 联动引起的各类兼容性问题。

## 修复内容

| Mod | 修复 | 说明 |
|-----|------|------|
| **Create** + **精妙背包** | 存量转信器支持精妙背包 | 向 `BackpackBlockEntity` 注入 `ThresholdSwitchObservable` 接口，使 Create 存量转信器（阈值开关）能直接读取精妙背包的库存量 |
| **Create** + **精妙背包** | 存量转信器 UI 显示修复 | 修复客户端存量转信器 UI 对精妙背包的显示问题，正确显示背包物品图标与库存信息 |
| **Create**（流体填充） | 无限流体填充死锁修复 | 修复 `fillInfinite=true` 时软管滑轮填充到一定量后不放置源块、不消耗流体的死锁问题 |
| **Create Stuff & Additions** | Block Picker 修复 | 禁止抓取多部分方块（门/床/双层植物），避免抓取一半后另一半残留；修复正常方块抓取时邻居方块更新 flags |
| **Quark** + **NeoContinuity** | 模型加载冲突修复 | 修复 TinyPotatoModel 在加载资源时与 NeoContinuity 的冲突，防止连接纹理加载失败 |

## 依赖

- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.228+
- **Create**: 6.0.9-215
- **Quark**: 4.1-477
- **Zeta**: 1.1-40
- **Sophisticated Backpacks**: 3.25.27.1528
- **Create Stuff & Additions**: 2.1.3

## 构建

```bash
./gradlew build
```

构建产物位于 `build/libs/`。
