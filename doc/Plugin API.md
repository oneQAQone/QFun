# Plugin API

## 简介

脚本引擎基于 `BeanShell` ，支持java8语法，不支持注解 
脚本目录：位于数据目录下的 `plugin` 文件夹中。

---

## 开发环境说明

本脚本运行于 **Modern BeanShell** 环境（支持 Java 8+）。编写时请遵循以下规范：

- **属性访问**：推荐使用成员符 `.` 直接访问对象属性（如 `msgData.msg`），无需调用 Getter 方法或者复杂的反射。

- **方法访问**：无需强制类型转换即可自动调用对应签名的方法和字段。

- **全局作用域**：`context`、`classLoader` 及所有 API 方法在脚本任意位置（含内部类、Lambda）均可直接调用，**无需传递**。

- **语法支持**：完整支持 Java 8 特性，包括 Lambda 表达式 (`->`)、流式 API (`Stream`)，此外，支持 Kotlin 空安全操作符 `?.`（安全调用）与 `?:`（Elvis 操作符）以简化空值处理。

- **线程模型**：事件回调默认在 **IO 线程** 执行。操作 UI 需切换至主线程，内置 `toast` 已自动处理线程切换。

- **宿主交互**：脚本持有宿主和模块的 `ClassLoader`，可直接 `import` 类并使用。

---

## Lambda 表达式支持

理论支持标准的 Java Lambda 语法，可用于简化代码或实现各类函数式接口（如 `Runnable`, `Comparator`, `Consumer` 等，以及脚本中自定义的符合单抽象方法的接口）。

### 1. 基础语法
支持 `->` 表达式，可用于单行语句或代码块。

```java
// 示例 1: 启动线程 (Runnable)
new Thread(() -> {
    // 处理逻辑...
    log("线程执行中");
}).start();

// 示例 2: 列表排序 (Comparator)
Collections.sort(list, (a, b) -> a.length() - b.length());

// 示例 3: 结合 Stream 使用
list.stream().filter(s -> s.startsWith("A")).count();
```

### 2. 方法引用 (::)
支持通过双冒号 `::` 引用现有的 Java 方法：

*   **静态方法**：如 `Math::max`
*   **实例方法**：如 `System.out::println`
*   **类名引用实例方法**：如 `String::toUpperCase`
*   **构造函数**：如 `ArrayList::new`

### 3. 引用脚本方法 (this::)
可以使用 `this` 关键字引用当前脚本中定义的方法（包括自定义方法和 API 内置方法），检测比较宽泛，参数数量与目标接口函数匹配即可，尝试调用失败时报错。

```java
// 定义一个脚本方法
public void handleItem(Object item) {
    log("处理: " + item);
}

// 在需要函数式接口的地方引用它
list.forEach(this::handleItem);

// 也可以直接引用 API 提供的内置方法
list.forEach(this::log);
```

---

## 脚本必须文件

- **main.java**：脚本执行入口
- **desc.txt**：脚本描述文件（可选）
- **info.prop**：配置文件  
  包含如下配置项：
  - `id` → 脚本唯一标识符
  - `pluginName` → 脚本名称
  - `author` → 作者
  - `versionCode` → 版本号

## 全局变量

| 变量名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `context` | `android.content.Context` | 宿主 App 全局上下文 |
| `pluginId` | `String` | 当前加载脚本 ID (**注意大小写**) |
| `classLoader` | `ClassLoader` | QQ 宿主类加载器 (HostClassLoader) |
| `pluginPath` | `String` | 当前加载脚本的文件夹绝对路径（注意无/） |
| `myUin` | `String` | 当前登录的 QQ 号 |

---

## 核心数据结构 (Java Beans)

### 1. MsgData (消息对象)
用于 `onMsg` 回调中。
- `int type`: 聊天类型 (1:好友/私聊, 2:群聊, 100:陌生人)
- `int msgType`: 消息类型
- `String peerUin`: 群号或好友QQ号
- `String peerUid`: 群号或好友UID
- `String userUin`: 发送者QQ号
- `String userUid`: 发送者UID
- `long time`: 发送时间戳 (秒)
- `long msgId`: 消息ID
- `String msg`: 文本消息内容 (包含 `[pic=url]` 等格式)
- `String path`: 文件/视频/语音保存路径
- `List<String> atList`: 消息中艾特的QQ号列表
- `Map<String, String> atMap`: 艾特映射表 (Key: Uin, Value: 艾特内容)
- `Object data`: 原始 `MsgRecord` 对象
- `Object contact`: 原始 `Contact` 对象

### 2. FriendInfo (好友信息)
- `String uin`: QQ号
- `String uid`: UID
- `String name`: 昵称
- `String remark`: 备注

### 3. GroupInfo (群信息)
- `String group`: 群号
- `String groupName`: 群名称
- `String groupOwner`: 群主QQ号
- `Object groupInfo`: 原始 `TroopInfo` 对象

### 4. MemberInfo (群成员信息)
- `String uin`: 成员QQ号
- `String uinName`: 群名片/昵称
- `int uinLevel`: 群等级
- `long joinGroupTime`: 入群时间戳
- `long lastActiveTime`: 最后发言时间戳
- `String role`: 角色 (OWNER:群主, ADMIN:管理员, MEMBER:成员)
- `Object memberInfo`: 原始 `TroopMemberInfo` 对象

### 5. ForbidInfo (禁言信息)
- `String user`: 被禁言成员QQ号
- `String userName`: 被禁言成员昵称
- `long time`: 剩余禁言时长 (秒)
- `long endTime`: 禁言结束时间戳

---

## 核心方法分类

### 一、消息相关方法

#### 1. sendMsg：发送消息
- **方法重载 1**：`sendMsg(String PeerUin, String 内容, int 聊天类型)`
  - 聊天类型：1好友 / 2群聊 / 100陌生人
  - 支持格式：
    - 艾特：`[atUin=QQ号]`（QQ号为0时表示艾特全体）
    - 图片：`[pic=图片链接或绝对路径]`
    - 可任意组合，将根据形式自动解析
- **方法重载 2**：`sendMsg(Object Contact对象, String 内容)`

#### 2. sendPic：发送图片
- **方法重载 1**：`sendPic(String PeerUin, String 图片路径, int 聊天类型)`
- **方法重载 2**：`sendPic(Object Contact对象, String 图片路径)`

#### 3. sendPtt：发送语音
- **方法重载 1**：`sendPtt(String PeerUin, String 语音路径, int 聊天类型)`
- **方法重载 2**：`sendPtt(Object Contact对象, String 语音路径)`

#### 4. sendCard：发送 JSON 卡片
- **方法重载 1**：`sendCard(String PeerUin, String JSON字符串, int 聊天类型)`
- **方法重载 2**：`sendCard(Object Contact对象, String JSON字符串)`

#### 5. sendFile：发送文件
- **方法重载 1**：`sendFile(String PeerUin, String 文件路径, int 聊天类型)`
- **方法重载 2**：`sendFile(Object Contact对象, String 文件路径)`

#### 6. sendVideo：发送视频
- **方法重载 1**：`sendVideo(String PeerUin, String 视频路径, int 聊天类型)`
- **方法重载 2**：`sendVideo(Object Contact对象, String 视频路径)`

#### 7. sendReplyMsg：发送引用回复
- **方法重载 1**：`sendReplyMsg(String PeerUin, long 引用消息ID, String 内容, int 聊天类型)`
- **方法重载 2**：`sendReplyMsg(Object Contact对象, long 引用消息ID, String 内容)`

#### 8. recallMsg：撤回消息
- **方法重载 1**：`recallMsg(int 聊天类型, String PeerUin, long 消息ID)`
- **方法重载 2**：`recallMsg(Object Contact对象, long 消息ID)`

#### 9. sendPai：拍一拍
- `sendPai(String 被拍者Uin, String PeerUin, int 聊天类型)`
  - `PeerUin`：在群里则是群号，私聊则是好友QQ

---

### 二、好友相关方法

#### 1. getAllFriend：获取好友列表
- `List<FriendInfo> getAllFriend()`
  - 返回 `FriendInfo` 对象列表 (见数据结构章节)。

#### 2. isFriend：判断好友
- `boolean isFriend(String uin)`

#### 3. sendZan：点赞
- `sendZan(String uin, int count)`

#### 4. Uin 与 Uid 转换
- `String getUidFromUin(String uin)`
- `String getUinFromUid(String uid)`

---

### 三、群管理与信息获取

#### 1. getGroupList：获取群列表
- `List<GroupInfo> getGroupList()`
  - 返回 `GroupInfo` 对象列表。

#### 2. getGroupMemberList：获取群成员列表
- `List<MemberInfo> getGroupMemberList(String 群号)`
  - 返回 `MemberInfo` 对象列表。

#### 3. getProhibitList：获取禁言列表
- `List<ForbidInfo> getProhibitList(String 群号)`
  - 返回 `ForbidInfo` 对象列表。

#### 4. getGroupInfo：获取单个群信息
- `TroopInfo getGroupInfo(String 群号)`
  - 返回原始 `TroopInfo` 对象。

#### 5. getMemberInfo：获取单个成员信息
- `MemberInfo getMemberInfo(String 群号, String 成员QQ)`

#### 6. shutUp：禁言成员
- `shutUp(String 群号, String 成员QQ, long 秒数)`
  - 传 0 为解禁。

#### 7. shutUpAll：全员禁言
- `shutUpAll(String 群号, boolean 是否开启)`

#### 8. kickGroup：踢出群成员
- `kickGroup(String 群号, String 成员QQ, boolean 是否拉黑)`
  - `是否拉黑`：不再接收此人申请。

#### 9. setGroupAdmin：设置管理员
- `setGroupAdmin(String 群号, String 成员QQ, boolean 是否设为管理)`

#### 10. setGroupMemberTitle：设置头衔 (仅群主)
- `setGroupMemberTitle(String 群号, String 成员QQ, String 头衔)`

#### 11. changeMemberName：修改群名片
- `changeMemberName(String 群号, String 成员QQ, String 新名片)`

#### 12. isShutUp：判断群是否全员禁言
- `boolean isShutUp(String 群号)`

#### 13. clockIn：群打卡
- `clockIn(String 群号)`

---

### 四、Cookie & Token 方法

- `String getSkey()`
- `String getRealSkey()`
- `String getPskey(String 域名)`
- `String getPt4Token(String 域名)`
- `String getStweb()`
- `String getGTK(String 域名)`
- `String getGroupRKey()`：群聊图片 RKey
- `String getFriendRKey()`：私聊图片 RKey
- `long getBkn(String key)`

---

### 五、数据存储方法

数据保存在 `plugin/脚本ID/config/` 下的 JSON 文件中。

#### 1. 写入数据
- `putString(String 配置名, String 键, String 值)`
- `putInt(String 配置名, String 键, int 值)`
- `putLong(String 配置名, String 键, long 值)`
- `putBoolean(String 配置名, String 键, boolean 值)`

#### 2. 读取数据
- `String getString(String 配置名, String 键, String 默认值)`
- `int getInt(String 配置名, String 键, int 默认值)`
- `long getLong(String 配置名, String 键, long 默认值)`
- `boolean getBoolean(String 配置名, String 键, boolean 默认值)`

---

### 六、回调方法 (main.java)

在脚本中实现以下方法以接收事件：

#### 1. onMsg：接收消息
- `void onMsg(Object msgData)`
  - 参数为 `MsgData` 对象。

#### 2. 群变动事件
- `void joinGroup(String 群号, String 成员QQ)`：成员入群
- `void quitGroup(String 群号, String 成员QQ)`：成员退群
- `void shutUpGroup(String 群号, String 成员QQ, long 时间, String 操作者QQ)`：群禁言事件

#### 3. 交互事件
- `void chatInterface(int 聊天类型, String PeerUin, String 名称)`：进入聊天界面
- `void onPaiYiPai(String PeerUin, int 聊天类型, String 操作者QQ)`：拍一拍事件

#### 4. 发送预处理
- `String getMsg(String 原始内容)`
  - 发送文本消息前触发，返回修改后的文本内容。

#### 5. 生命周期
- `void unLoadPlugin()`：脚本停止/卸载时触发。

---

### 七、菜单功能

#### 1. 脚本菜单 (悬浮窗)
- **添加**：`addItem(String 菜单名, String 回调方法名)`
- **回调定义**：
  ```java
  // 3参数版本
  void 回调方法名(int 聊天类型, String PeerUin, String 名称) { ... }
  
  // 4参数版本 (包含 Contact 对象)
  void 回调方法名(int 聊天类型, String PeerUin, String 名称, Object contact) { ... }
  ```
#### 2. 消息菜单 (长按消息)
- **添加**：`addMenuItem(String 菜单名, String 回调方法名, int[] 消息类型数组)`
  - 最后一个参数为空或不写则默认所有消息
- **回调定义**：
  ```java
  void 回调方法名(Object msgData) { 
      //msgData为MsgData对象
  }
  ```

### 八、其他辅助方法

#### 1. 日志与提示
- `log(String 文件名 = "log.txt", String 内容)`：追加写入日志到脚本目录
- `toast(Object 内容)`：系统 Toast
- `qqToast(int 图标类型, Object 内容)`：QQ 风格顶部弹窗
  - 图标：0=警告, 1=错误/失败, 2=成功

#### 2. 动态加载
- `loadJava(String java文件路径)`
- `loadJar(String jar文件路径)`
- `loadDex(String dex文件路径)`
- `registerActivity(Class<? extends Activity> 类)`
  - 注册后可使用startActivity启动
  
> jar，dex加载后可直接使用import导入

#### 3. 界面
- `Activity getNowActivity()`：获取当前顶层 Activity (可能为 null)