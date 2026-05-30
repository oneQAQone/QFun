<div align="center">
    <h1>QFun</h1>

![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=android&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![GitHub Stars](https://img.shields.io/github/stars/oneQAQone/QFun?style=social)

</div>

# 简介

一款基于 Xposed 框架开发的 QQ/TIM 功能增强模块。
本项目采用了 **Kotlin** + **Jetpack Compose** 的现代 Android 技术栈构建。

# 功能列表

### 模块功能
仅列举部分代表性功能，更多实用功能请在模块内探索：
- [x] 群打卡
- [x] 防撤回 (带提示)
- [x] 自动续火
- [x] 消息复读 (+1)
- [x] 闪照破解
- [x] 屏蔽艾特全体
- [x] 简洁群管菜单
- [x] 一键点赞
- [x] 上传 APK 重命名
- [x] 去除回复自动艾特
- [x] 平板模式
- [x] 显示精确消息时间
- [x] 自定义骰子/猜拳/投篮
- [x] 移除表情回应
- [x] 解除风险网页拦截

> 💡 如有其他功能需求欢迎提交反馈，具备可行性的功能将被加入开发计划。

### 脚本扩展与编写
模块内置基于 **BeanShell** 的脚本引擎，支持使用 **Java 语法** 编写脚本以动态扩展功能，可自行编写或从在线脚本库下载。

*   **事件驱动**：实时监听消息收发、群成员变动及社交交互等事件，用于触发自动化逻辑。
*   **QQ接口**：封装基于NT架构的 QQ 操作 API 及数据获取接口，提供便捷的底层调用能力。
*   **交互集成**：支持在消息长按菜单及脚本悬浮窗中注册自定义功能入口，增强交互体验。
*   **动态加载**：提供运行时加载外部类库或 Java 源码的能力，实现灵活的功能热插拔。

# 技术栈

### 💻 核心语言与架构
*   **Kotlin**: 项目逻辑与 UI 代码主要采用 Kotlin 编写。充分利用 **Coroutines**（协程）处理复杂的异步任务（如网络请求、IO 操作），确保主线程流畅不卡顿。
*   **MVVM**: 采用 Model-View-ViewModel 架构设计，实现 UI 状态与业务逻辑的解耦。

### 🎨 界面与交互
*   **Jetpack Compose (Material3)**: 摒弃传统 XML，基于 Google 最新设计规范构建的全声明式 UI，提供沉浸式视觉体验与动态主题适配。

### 🛠 逆向与 Hook
*   **DexKit**: 集成高效的 C++ 运行时字节码分析库，通过特征匹配而非硬编码查找 Hook 点，极大提升了模块在宿主更新后的存活率（抗混淆）。
*   **Xposed API**: 采用 LibXposed 标准接口并兼容 Legacy Xposed，确保跨框架的稳定性与高性能。

### 📦 数据与构建
*   **Kotlin Serialization**: 官方高性能序列化库，处理配置文件与网络数据的 JSON 读写，确保类型安全。
*   **KSP**: 使用 Kotlin Symbol Processing 在编译时自动扫描注解并生成 Hook 注册表，实现模块功能的解耦与自动装载。

### 🔌 动态扩展
*   **BeanShell**: 内置轻量级 Java 脚本解释器，支持用户编写脚本动态调用模块 API，实现功能的热插拔与扩展。

# 适配与运行环境

### Android 系统
*   **最低版本**: Android 8.0 (API Level 26)
*   **推荐版本**: Android 11.0+ (以获得最佳的 UI 适配体验)
*   **架构支持**: `arm64-v8a` (主流), `armeabi-v7a`。**暂不支持 x86 环境**（部分模拟器无法使用）。

### 宿主应用
| 应用 | 推荐版本 | 备注 |
| :--- | :--- | :--- |
| **QQ** | `v9.1.25` 及以上 | 其他基于 NT 架构的版本兼容性需自行测试 |
| **TIM** | `v4.0.95` 及以上 | 针对旧版架构做了部分兼容 |
> ⚠️ 新增功能主要基于最新版 QQ 开发，旧版本可能存在兼容性问题。

### 框架支持

| 环境类型 | 推荐方案 | 说明 |
| :--- | :--- | :--- |
| **✅ Root 环境** | **LSPosed (Zygisk/Riru)** | **强烈推荐**。支持 Scope 作用域模式，性能损耗最小，Hook 稳定性最高。 |
| **🛡️ 免 Root 环境** | **LSPatch 及主流免 Root 框架** | **推荐**。通过修补 APK 的方式集成 Xposed 环境，适合无法解锁 Bootloader 的设备。 |
| *其他环境* | *EdXposed / 太极 / VMOS* | *理论支持*，但属于旧一代技术或容器环境，可能存在兼容性问题，未做全面测试。 |

# 反馈与日志

为了高效定位问题，反馈时**请务必注明**以下信息：
1.  **宿主版本**
2.  **模块版本**
3.  **运行框架及版本**

> **💡 提示**：`Android/data/[宿主包名]/QFun/global/log/` 目录下的 **environment_info.txt** 已自动记录了完整的运行环境信息，建议在反馈时一同提交。
> 您也可以直接打包并反馈 **LSPosed 框架日志**（建议开启详细日志）。

### 1. 常规错误
> 指功能异常、脚本报错等未导致应用闪退的情况。
*   **文件**: `error_log.txt`
*   **位置**: `Android/data/[宿主包名]/QFun/[当前QQ号]/log/`

### 2. 应用崩溃
> 指应用直接停止运行、闪退的情况。
*   **文件**: `crash_[时间戳].zip`
*   **位置**: `Android/data/[宿主包名]/QFun/[当前QQ号]/crash/`
    *   *(注：若未登录即闪退，请检查 `.../QFun/global/crash/` 目录)*
*   **提示**: 闪退弹窗中**点击路径文字**即可直接复制完整路径。

### ⚠️ 关于路径
请在设备的**内部存储**（若是应用分身/多开，则在对应的**分身存储**）中查找上述路径。

<br/>

<div align="center">

### 致谢

本项目借鉴了一些开源项目，特别感谢以下开源项目提供的底层支持、架构参考及代码灵感：

| Open Source Project | Role & Description |
| :--- | :--- |
| [![LSPosed](https://img.shields.io/badge/LSPosed-Framework-blue?style=flat-square&logo=android)](https://github.com/LSPosed/LSPosed) | **现代化的 Xposed 框架**<br>提供了稳定、高效且支持作用域的运行环境。 |
| [![DexKit](https://img.shields.io/badge/DexKit-Analysis-orange?style=flat-square&logo=c%2B%2B)](https://github.com/LuckyPray/DexKit) | **Native 级动态分析库**<br>赋予模块强大的运行时字节码查找与抗混淆能力。 |
| [![Compose](https://img.shields.io/badge/Jetpack_Compose-UI_Toolkit-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose) | **现代化 UI 工具包**<br>构建了模块美观、流畅且支持动态主题的用户界面。 |
| [![LibXposed](https://img.shields.io/badge/LibXposed-Next_Gen_API-green?style=flat-square&logo=android)](https://github.com/libxposed/api) | **下一代 Hook 标准**<br>提供了跨框架兼容的底层 API 接口支持。 |
| [![BeanShell](https://img.shields.io/badge/BeanShell-Script_Engine-brown?style=flat-square&logo=openjdk&logoColor=white)](https://github.com/beanshell/beanshell) | **轻量级 Java 脚本引擎**<br>提供了模块内置的动态脚本执行能力，支持用户通过编写脚本灵活扩展功能。 |
| [![QAuxiliary](https://img.shields.io/badge/QAuxiliary-Architecture-8A2BE2?style=flat-square&logo=github)](https://github.com/cinit/QAuxiliary) | **架构兼容与注入实现**<br>借鉴了 Activity 代理注入及资源加载的成熟方案以及双框架支持，并参考了其多处核心 Hook 逻辑与代码实现。 |
| [![TCQT](https://img.shields.io/badge/TCQT-Interfaces-F7DF1E?style=flat-square&logo=github&logoColor=black)](https://github.com/callng/TCQT) | **编译时接口与逻辑参考**<br>借鉴了关键业务类的编译时接口定义，同时参考了其部分 Hook 点位分析与功能实现写法。 |

</div>

<br/>

# 免责声明

1.  **仅供学习交流**: 本项目开发初衷仅为 Android 开发与逆向工程技术的学习、交流与研究。
2.  **风险自担**: 使用本模块可能会违反 QQ/TIM 的用户协议，存在导致账号被冻结、封禁或功能受限的风险。**开发者不对因使用本模块造成的任何账号损失、数据丢失或其他后果负责。**
3.  **非商业用途**: 本项目完全免费开源，禁止任何人将本项目用于商业用途或非法用途。

**如果您下载、安装或使用了本模块，即代表您已阅读并同意上述免责声明。**

<br/>

<div align="center">

*Made with ❤️ by [oneQAQone](https://github.com/oneQAQone)*

</div>
