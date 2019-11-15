package com.yudu.snmp.job;

import com.yudu.snmp.config.SmsConfig;
import com.yudu.snmp.config.SnmpConfig;
import com.yudu.snmp.pojo.SmsOut;
import com.yudu.snmp.repository.SmsOutRepository;
import com.yudu.snmp.util.Snmp4jUtils;
import com.yudu.snmp.util.SnmpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class SnmpTask {
    @Autowired
    private SnmpConfig snmpConfig;
    @Autowired
    private SmsConfig smsConfig;
    @Autowired
    private SmsOutRepository smsOutRepository;
    //job
    public void snmp(){
        String[] ipArr = snmpConfig.ips.split(",");
        for (int i = 0; i <ipArr.length; i++) {
            long cpuRateUse = Snmp4jUtils.cpuRateUse(ipArr[i]);
            long memoryRateUse = Snmp4jUtils.memoryRateUse(ipArr[i]);
            LinkedHashMap<String,Long> diskRateUse = Snmp4jUtils.diskRateUse(ipArr[i]);
            List<String> processList = Snmp4jUtils.processInfo(ipArr[i]);
            List<String> portList = Snmp4jUtils.portInfo(ipArr[i]);
            //默认该服务器正常
            boolean ipBoolean = true;
            //先判断异常的情况，在判断正常的情况
            if(cpuRateUse > snmpConfig.cpuRateUseThreshold){
                ipBoolean = false;
                smsConfig.smsTemplate = smsConfig.smsTemplate.replace("【cpuRateUse】",cpuRateUse+"")
                        .replace("【cpuRateUseThreshold】",snmpConfig.cpuRateUseThreshold+"");
            }else {
                smsConfig.smsTemplate = smsConfig.smsTemplate.replace("，CPU使用率【cpuRateUse】超过预警值【cpuRateUseThreshold】","");
            }
            if (memoryRateUse > snmpConfig.memoryRateUseThreshold){
                ipBoolean = false;
                smsConfig.smsTemplate = smsConfig.smsTemplate.replace("【memoryRateUse】",memoryRateUse+"")
                        .replace("【memoryRateUseThreshold】",snmpConfig.memoryRateUseThreshold+"");
            }else {
                smsConfig.smsTemplate = smsConfig.smsTemplate.replace("，内存使用率【memoryRateUse】超过预警值【memoryRateUseThreshold】","");
            }
            String diskString = "";
            for (String k:diskRateUse.keySet()) {
                if(diskRateUse.get(k) > snmpConfig.diskRateUseThreshold){
                    diskString+= k+"盘使用率"+diskRateUse.get(k)+"超过预警值"+snmpConfig.diskRateUseThreshold+",";
                }
            }
           if (!diskString.equals("")){
               ipBoolean = false;
               //diskString = diskString.substring(0,diskString.length() - 1);
               smsConfig.smsTemplate = smsConfig.smsTemplate.replace("【盘使用率】",diskString);
           }else{
               smsConfig.smsTemplate = smsConfig.smsTemplate.replace("，【盘使用率】","");
           }
           boolean processBoolean = false;
           for (int j = 0; j < processList.size(); j++) {
                if (processList.get(j).equals("w3wp.exe")){
                    processBoolean = true;
                    return;
                }
            }
           if (!processBoolean){
               smsConfig.smsTemplate = smsConfig.smsTemplate.replace("，IIS服务停止运行","");
           }else{
               ipBoolean = false;
           }
           boolean portBoolean = false;
           for (int j = 0; j < portList.size(); j++) {
                if (portList.get(j).equals("8080")){
                    portBoolean = true;
                    return;
                }
           }
           if (!portBoolean){
                smsConfig.smsTemplate = smsConfig.smsTemplate.replace("，OA系统停止运行","");
            }else{
               ipBoolean = false;
           }
           //判断是否发送短信
           if (!ipBoolean){
               smsConfig.smsTemplate = smsConfig.smsTemplate.replace("【ip】",ipArr[i]);
               String[] iphoneArr = snmpConfig.iphones.split(",");
               for (int j = 0; j < iphoneArr.length; j++) {
                   SmsOut smsOut = new SmsOut();
                   smsOut.setId(UUID.randomUUID().toString().replace("-",""));
                   smsOut.setContext(smsConfig.smsTemplate);
                   smsOut.setDate(new Date());
                   smsOut.setIsSuccess(0);
                   smsOut.setIsVisible(false);
                   smsOut.setrPerson("无");
                   smsOut.setrPersonId("");
                   smsOut.setType(5);
                   smsOut.setrNum(iphoneArr[j]);
                   smsOutRepository.insert(smsOut);
               }
           }
        }
    }
}
