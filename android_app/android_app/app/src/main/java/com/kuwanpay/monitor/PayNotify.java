package com.kuwanpay.monitor;

/**
 * 收款通知数据类
 */
public class PayNotify {
    
    public String payType;      // 支付类型：alipay, wxpay, qqpay
    public String platform;     // 平台名称：支付宝，微信，QQ
    public double amount;       // 金额
    public String rawText;      // 原始通知文本
    public long timestamp;      // 时间戳
    
    @Override
    public String toString() {
        return "PayNotify{" +
                "payType='" + payType + '\'' +
                ", platform='" + platform + '\'' +
                ", amount=" + amount +
                ", rawText='" + rawText + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
