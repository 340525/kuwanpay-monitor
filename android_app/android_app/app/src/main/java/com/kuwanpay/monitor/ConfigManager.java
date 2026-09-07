package com.kuwanpay.monitor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 配置管理器
 * 保存和读取服务器配置
 */
public class ConfigManager {
    
    private static final String PREFS_NAME = "kuwanpay_config";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String KEY_CHANNEL_ID = "channel_id";
    private static final String KEY_APP_KEY = "app_key";
    
    private SharedPreferences prefs;
    private Context context;
    
    public ConfigManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 获取服务器地址
     */
    public String getServerUrl() {
        return prefs.getString(KEY_SERVER_URL, "https://hongyzf.com/codepay_notify.php");
    }
    
    /**
     * 设置服务器地址
     */
    public void setServerUrl(String url) {
        prefs.edit().putString(KEY_SERVER_URL, url).apply();
    }
    
    /**
     * 获取通道 ID
     */
    public String getChannelId() {
        return prefs.getString(KEY_CHANNEL_ID, "");
    }
    
    /**
     * 设置通道 ID
     */
    public void setChannelId(String channelId) {
        prefs.edit().putString(KEY_CHANNEL_ID, channelId).apply();
    }
    
    /**
     * 获取 APP 密钥
     */
    public String getAppKey() {
        return prefs.getString(KEY_APP_KEY, "");
    }
    
    /**
     * 设置 APP 密钥
     */
    public void setAppKey(String appKey) {
        prefs.edit().putString(KEY_APP_KEY, appKey).apply();
    }
    
    /**
     * 保存配置
     */
    public void saveConfig(String serverUrl, String channelId, String appKey) {
        prefs.edit()
            .putString(KEY_SERVER_URL, serverUrl)
            .putString(KEY_CHANNEL_ID, channelId)
            .putString(KEY_APP_KEY, appKey)
            .apply();
    }
}
