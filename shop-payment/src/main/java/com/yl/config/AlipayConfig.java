package com.yl.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @param
 * @return
 */
@Configuration
@PropertySource("classpath:alipay.properties")
public class AlipayConfig {
    // 请求网关地址
    @Value("${alipay_url}")
    private String alipay_url;

    @Value("${app_private_key}")
    private String app_private_key;

    @Value("${alipay_public_key}")
    private String alipay_public_key;

    @Value("${app_id}")
    private String app_id;

    private static String format = "json";
    private static String charset = "utf-8";
    private static String sign_type = "RSA2";

    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    @Value("${return_payment_url}")
    private String return_payment_url;
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    @Value("${notify_payment_url}")
    private String notify_payment_url;

    @Bean
    public AlipayClient alipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient(alipay_url, app_id, app_private_key, format, charset, alipay_public_key, sign_type);
        return alipayClient;
    }
}