package com.kuwanpay.monitor;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * 酷玩支付监控助手 - 主界面
 */
public class MainActivity extends AppCompatActivity {
    
    private EditText etServerUrl;
    private EditText etChannelId;
    private EditText etAppKey;
    private Button btnSave;
    private Button btnStartService;
    private TextView tvStatus;
    
    private ConfigManager configManager;
    private PayReceiver payReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        configManager = new ConfigManager(this);
        
        // 初始化视图
        etServerUrl = findViewById(R.id.et_server_url);
        etChannelId = findViewById(R.id.et_channel_id);
        etAppKey = findViewById(R.id.et_app_key);
        btnSave = findViewById(R.id.btn_save);
        btnStartService = findViewById(R.id.btn_start_service);
        tvStatus = findViewById(R.id.tv_status);
        
        // 加载配置
        loadConfig();
        
        // 保存配置
        btnSave.setOnClickListener(v -> saveConfig());
        
        // 启动服务
        btnStartService.setOnClickListener(v -> startAccessibilityService());
        
        // 注册广播接收器
        payReceiver = new PayReceiver();
        IntentFilter filter = new IntentFilter("com.kuwanpay.PAY_RECEIVED");
        registerReceiver(payReceiver, filter);
    }
    
    /**
     * 加载配置
     */
    private void loadConfig() {
        etServerUrl.setText(configManager.getServerUrl());
        etChannelId.setText(configManager.getChannelId());
        etAppKey.setText(configManager.getAppKey());
        
        // 检查服务状态
        checkServiceStatus();
    }
    
    /**
     * 保存配置
     */
    private void saveConfig() {
        String serverUrl = etServerUrl.getText().toString().trim();
        String channelId = etChannelId.getText().toString().trim();
        String appKey = etAppKey.getText().toString().trim();
        
        if (serverUrl.isEmpty()) {
            Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (appKey.isEmpty()) {
            Toast.makeText(this, "请输入 APP 密钥", Toast.LENGTH_SHORT).show();
            return;
        }
        
        configManager.saveConfig(serverUrl, channelId, appKey);
        Toast.makeText(this, "配置已保存", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 启动无障碍服务
     */
    private void startAccessibilityService() {
        // 检查服务是否已启用
        if (isServiceEnabled()) {
            Toast.makeText(this, "监控服务已启动", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 跳转到无障碍服务设置页面
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "请开启「酷玩支付监控」服务", Toast.LENGTH_LONG).show();
    }
    
    /**
     * 检查服务是否已启用
     */
    private boolean isServiceEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> services = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        
        for (AccessibilityServiceInfo service : services) {
            if (service.getResolveInfo().serviceInfo.packageName.equals(getPackageName())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查服务状态
     */
    private void checkServiceStatus() {
        if (isServiceEnabled()) {
            tvStatus.setText("监控状态：运行中");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvStatus.setText("监控状态：未启动");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
    
    /**
     * 收款广播接收器
     */
    private class PayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String notify = intent.getStringExtra("notify");
            Toast.makeText(MainActivity.this, "收到收款：" + notify, Toast.LENGTH_LONG).show();
            checkServiceStatus();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        checkServiceStatus();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (payReceiver != null) {
            unregisterReceiver(payReceiver);
        }
    }
}
