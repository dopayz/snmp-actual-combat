package com.yudu.snmp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DbConfig {
    @Value("${spring.datasource.url}")
    public String dbUrl;
    @Value("${spring.datasource.username}")
    public String dbUserName;
    @Value("${spring.datasource.password}")
    public String dbPassword;
    @Value("${spring.datasource.driver-class-name}")
    public String dbDriverClassName;
}
