package com.yudu.snmp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnmpConfig {
    @Value("${cpuRateUseThreshold}")
    public long cpuRateUseThreshold;
    @Value("${memoryRateUseThreshold}")
    public long memoryRateUseThreshold;
    @Value("${diskRateUseThreshold}")
    public long diskRateUseThreshold;
    @Value("${ips}")
    public String ips;
    @Value("${iphones}")
    public String iphones;
}
