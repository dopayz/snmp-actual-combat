package com.yudu.snmp;

import com.yudu.snmp.util.SnmpUtils;

public class SnmpTest {
    public static void main(String[] args) {
        String ip="192.168.1.166";
        System.out.println(SnmpUtils.cpuRateUse(ip));
//        System.out.println(SnmpUtils.memoryRateUse(ip));
//        System.out.println(SnmpUtils.diskRateUse(ip));
//        System.out.println(SnmpUtils.processInfo(ip));
//        System.out.println(SnmpUtils.portInfo(ip));
    }
}
