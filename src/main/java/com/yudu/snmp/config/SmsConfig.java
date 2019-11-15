package com.yudu.snmp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsConfig {
	@Value("${smsTemplate}")
	public String smsTemplate;
}
