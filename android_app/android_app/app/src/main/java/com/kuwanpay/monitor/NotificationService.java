package com.kuwanpay.monitor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 酷玩支付通知监听服务
 * 使用无障碍服务监听支付宝/微信/QQ的收款通知
 */
public class NotificationService extends AccessibilityService {
    
    private static final String TAG = "KuwanPayMonitor";
    
    // 支付宝包名
    private static final String PACKAGE_ALIPAY = "com.eg.android.AlipayGphone";
    // 微信包名
    private static final String PACKAGE_WECHAT = "com.tencent.mm";
    // QQ包名
    private static final String PACKAGE_QQ = "com.tencent.mobileqq";
    
    // 金额正则表达式
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(\\d+\\.?\\d*)元");
    
    private ConfigManager configManager;
    
    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "通知监听服务已启动");
        configManager = new ConfigManager(this);
        Toast.makeText(this, "酷玩支付监控已启动", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        
        // 只处理通知栏事件
        if (eventType != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            return;
        }
        
        String packageName = event.getPackageName().toString();
        CharSequence text = event.getText().isEmpty() ? "" : event.getText().get(0);
        
        if (text == null || text.length() == 0) {
            return;
        }
        
        String notifyText = text.toString();
        Log.d(TAG, "收到通知：" + packageName + " - " + notifyText);
        
        // 判断是否是收款通知
        PayNotify notify = parseNotify(packageName, notifyText);
        if (notify != null) {
            Log.i(TAG, "检测到收款：" + notify.toString());
            
            // 回调服务器
            notifyServer(notify);
            
            // 发送广播通知主界面
            Intent intent = new Intent("com.kuwanpay.PAY_RECEIVED");
            intent.putExtra("notify", notify.toString());
            sendBroadcast(intent);
        }
    }
    
    /**
     * 解析收款通知
     */
    private PayNotify parseNotify(String packageName, String text) {
        PayNotify notify = new PayNotify();
        
        // 判断支付类型
        if (packageName.contains(PACKAGE_ALIPAY)) {
            notify.payType = "alipay";
            notify.platform = "支付宝";
        } else if (packageName.contains(PACKAGE_WECHAT)) {
            notify.payType = "wxpay";
            notify.platform = "微信";
        } else if (packageName.contains(PACKAGE_QQ)) {
            notify.payType = "qqpay";
            notify.platform = "QQ";
        } else {
            return null;
        }
        
        // 判断是否是收款通知
        if (!text.contains("收款") && !text.contains("到账") && !text.contains("收到")) {
            return null;
        }
        
        // 提取金额
        Matcher matcher = AMOUNT_PATTERN.matcher(text);
        if (matcher.find()) {
            notify.amount = Double.parseDouble(matcher.group(1));
            notify.rawText = text;
            notify.timestamp = System.currentTimeMillis() / 1000;
            return notify;
        }
        
        return null;
    }
    
    /**
     * 回调服务器
     */
    private void notifyServer(PayNotify notify) {
        String serverUrl = configManager.getServerUrl();
        String channelId = configManager.getChannelId();
        String appKey = configManager.getAppKey();
        
        if (serverUrl.isEmpty() || appKey.isEmpty()) {
            Log.e(TAG, "服务器配置未设置");
            return;
        }
        
        // 构建请求参数
        String tradeNo = matchOrderByAmount(notify.amount);
        
        if (tradeNo.isEmpty()) {
            Log.w(TAG, "未找到匹配的订单，金额：" + notify.amount);
            return;
        }
        
        // 生成签名
        String signData = tradeNo + notify.amount + appKey;
        String sign = md5(signData);
        
        // 发送 HTTP 请求
        new Thread(() -> {
            try {
                HttpHelper http = new HttpHelper();
                http.addParam("trade_no", tradeNo);
                http.addParam("money", String.valueOf(notify.amount));
                http.addParam("sign", sign);
                http.addParam("channel_id", channelId);
                http.addParam("api_trade_no", "MONITOR_" + notify.timestamp);
                http.addParam("buyer", notify.platform + "用户");
                
                String result = http.post(serverUrl);
                Log.i(TAG, "服务器响应：" + result);
                
            } catch (Exception e) {
                Log.e(TAG, "回调服务器失败", e);
            }
        }).start();
    }
    
    /**
     * 根据金额匹配订单
     */
    private String matchOrderByAmount(double amount) {
        try {
            HttpHelper http = new HttpHelper();
            http.addParam("amount", String.valueOf(amount));
            http.addParam("channel_id", configManager.getChannelId());
            
            String result = http.get(configManager.getServerUrl() + "?act=get_pending_order");
            // 解析返回的订单号
            // TODO: 实现订单匹配逻辑
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }
    
    /**
     * MD5 加密
     */
    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    @Override
    public void onInterrupt() {
        Log.w(TAG, "通知监听服务被中断");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "通知监听服务已销毁");
    }
}
