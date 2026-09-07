# 酷玩支付监控助手

## 项目说明

这是一个 Android 应用，用于监控微信/支付宝/QQ 的收款通知，自动回调服务器确认订单。

## 功能特点

- ✅ 监听微信/支付宝/QQ 收款通知
- ✅ 自动提取金额
- ✅ 自动回调服务器
- ✅ 支持多通道配置
- ✅ 后台常驻运行

## 编译步骤

### 1. 准备环境

- 安装 Android Studio（https://developer.android.com/studio）
- 确保已安装 JDK 8 或更高版本

### 2. 导入项目

1. 打开 Android Studio
2. 点击 "Open"
3. 选择本项目文件夹
4. 等待 Gradle 同步完成

### 3. 修改配置（可选）

在 `ConfigManager.java` 中修改默认配置：

```java
public String getServerUrl() {
    return prefs.getString(KEY_SERVER_URL, "https://hongyzf.com/codepay_notify.php");
}
```

### 4. 编译 APK

1. 点击菜单 "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
2. 等待编译完成（约 5-10 分钟）
3. APK 文件位置：`app/build/outputs/apk/debug/app-debug.apk`

### 5. 安装到手机

1. 将 APK 文件传输到手机
2. 在手机上安装 APK
3. 打开 APP，配置服务器地址和 APP 密钥
4. 点击"启动监控服务"
5. 在设置中开启无障碍服务

## 使用说明

### 配置服务器

1. 打开 APP
2. 填写服务器地址：`https://你的域名/codepay_notify.php`
3. 填写通道 ID（可选）
4. 填写 APP 密钥
5. 点击"保存配置"

### 启动监控

1. 点击"启动监控服务"
2. 在设置中找到"酷玩支付监控"
3. 开启服务
4. 返回 APP，确认状态显示"运行中"

### 测试

1. 创建一个测试订单
2. 使用微信/支付宝扫码付款
3. 查看 APP 是否收到通知
4. 查看服务器订单状态是否更新

## 注意事项

- ⚠️ 需要保持 APP 在后台运行
- ⚠️ 需要开启无障碍服务权限
- ⚠️ 需要允许 APP 自启动（部分手机需要）
- ️ 微信/支付宝的收款通知格式可能变化

## 技术支持

如有问题，请联系酷玩支付客服。

## 版本历史

- v1.0 (2025-01-01)
  - 初始版本
  - 支持微信/支付宝/QQ 监控
  - 自动回调服务器
