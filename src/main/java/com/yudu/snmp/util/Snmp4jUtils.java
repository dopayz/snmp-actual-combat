package com.yudu.snmp.util;

import org.snmp4j.*;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.*;

//snmp4j工具类
public class Snmp4jUtils {
    private static final int COLON_SIZE = 4;
    //获取cpu使用率
    public static long cpuRateUse(String ip){
        long result = -1;
        TransportMapping transport = null ;
        Snmp snmp = null ;
        CommunityTarget target;
        String[] oids = {"1.3.6.1.2.1.25.3.3.1.2"};
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);//创建snmp
            snmp.listen();//监听消息
            target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setRetries(2);
            target.setAddress(GenericAddress.parse("udp:"+ip+"/161"));
            target.setTimeout(8000);
            target.setVersion(SnmpConstants.version2c);
            TableUtils tableUtils = new TableUtils(snmp, new PDUFactory() {
                @Override
                public PDU createPDU(Target arg0) {
                    PDU request = new PDU();
                    request.setType(PDU.GET);
                    return request;
                }

                @Override
                public PDU createPDU(MessageProcessingModel messageProcessingModel) {
                    return null;
                }
            });
            OID[] columns = new OID[oids.length];
            for (int i = 0; i < oids.length; i++)
                columns[i] = new OID(oids[i]);
            List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
            if(list.size()==1 && list.get(0).getColumns()==null){
                return result;
            }else {
                int percentage = 0;
                for (TableEvent event : list) {
                    VariableBinding[] values = event.getColumns();
                    if (values != null)
                        percentage += Integer.parseInt(values[0].getVariable().toString());
                }
                result = (long)percentage / list.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (transport != null)
                    transport.close();
                if (snmp != null)
                    snmp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    //获取内存使用率
    public static long memoryRateUse(String ip){
        long result = -1;
        TransportMapping transport;
        transport = null;
        Snmp snmp = null ;
        CommunityTarget target;
        String[] oids = {"1.3.6.1.2.1.25.2.3.1.2",  //type 存储单元类型
                "1.3.6.1.2.1.25.2.3.1.3",  //descr
                "1.3.6.1.2.1.25.2.3.1.4",  //unit 存储单元大小
                "1.3.6.1.2.1.25.2.3.1.5",  //size 总存储单元数
                "1.3.6.1.2.1.25.2.3.1.6"}; //used 使用存储单元数;
        String PHYSICAL_MEMORY_OID = "1.3.6.1.2.1.25.2.1.2";//物理存储
        String VIRTUAL_MEMORY_OID = "1.3.6.1.2.1.25.2.1.3"; //虚拟存储
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);//创建snmp
            snmp.listen();//监听消息
            target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setRetries(2);
            target.setAddress(GenericAddress.parse("udp:"+ip+"/161"));
            target.setTimeout(8000);
            target.setVersion(SnmpConstants.version2c);
            TableUtils tableUtils = new TableUtils(snmp, new PDUFactory() {
                @Override
                public PDU createPDU(Target arg0) {
                    PDU request = new PDU();
                    request.setType(PDU.GET);
                    return request;
                }

                @Override
                public PDU createPDU(MessageProcessingModel messageProcessingModel) {
                    return null;
                }
            });
            OID[] columns = new OID[oids.length];
            for (int i = 0; i < oids.length; i++)
                columns[i] = new OID(oids[i]);
            @SuppressWarnings("unchecked")
            List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
            if(list.size()==1 && list.get(0).getColumns()==null){
                return -1;
            }else{
                for(TableEvent event : list){
                    VariableBinding[] values = event.getColumns();
                    if(values == null) continue;
                    //int unit = Integer.parseInt(values[2].getVariable().toString());//unit 存储单元大小
                    int totalSize = Integer.parseInt(values[3].getVariable().toString());//size 总存储单元数
                    int usedSize = Integer.parseInt(values[4].getVariable().toString());//used  使用存储单元数
                    String oid = values[0].getVariable().toString();
                    if (PHYSICAL_MEMORY_OID.equals(oid)){
                        result =  (long)usedSize*100/totalSize;
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(transport!=null)
                    transport.close();
                if(snmp!=null)
                    snmp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    //获取磁盘使用率getChinese(values[1].getVariable().toString()).split(":")[0]
    public static LinkedHashMap<String,Long> diskRateUse(String ip){
        LinkedHashMap<String,Long> result = new LinkedHashMap<>();
        TransportMapping transport = null;
        Snmp snmp = null ;
        CommunityTarget target;
        String DISK_OID = "1.3.6.1.2.1.25.2.1.4";
        String[] oids = {"1.3.6.1.2.1.25.2.3.1.2",  //type 存储单元类型
                "1.3.6.1.2.1.25.2.3.1.3",  //descr
                "1.3.6.1.2.1.25.2.3.1.4",  //unit 存储单元大小
                "1.3.6.1.2.1.25.2.3.1.5",  //size 总存储单元数
                "1.3.6.1.2.1.25.2.3.1.6"}; //used 使用存储单元数;
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);//创建snmp
            snmp.listen();//监听消息
            target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setRetries(2);
            target.setAddress(GenericAddress.parse("udp:"+ip+"/161"));
            target.setTimeout(8000);
            target.setVersion(SnmpConstants.version2c);
            TableUtils tableUtils = new TableUtils(snmp, new PDUFactory() {
                @Override
                public PDU createPDU(Target arg0) {
                    PDU request = new PDU();
                    request.setType(PDU.GET);
                    return request;
                }

                @Override
                public PDU createPDU(MessageProcessingModel messageProcessingModel) {
                    return null;
                }
            });
            OID[] columns = new OID[oids.length];
            for (int i = 0; i < oids.length; i++)
                columns[i] = new OID(oids[i]);
            @SuppressWarnings("unchecked")
            List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
            if(list.size()==1 && list.get(0).getColumns()==null){
                return result;
            }else{
                for(TableEvent event : list){
                    VariableBinding[] values = event.getColumns();
                    if(values == null ||!DISK_OID.equals(values[0].getVariable().toString()))
                        continue;
                    //int unit = Integer.parseInt(values[2].getVariable().toString());//unit 存储单元大小
                    int totalSize = Integer.parseInt(values[3].getVariable().toString());//size 总存储单元数
                    int usedSize = Integer.parseInt(values[4].getVariable().toString());//used  使用存储单元数
                    result.put(getChinese(values[1].getVariable().toString()).split(":")[0],(long)usedSize*100/totalSize);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(transport!=null)
                    transport.close();
                if(snmp!=null)
                    snmp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    //获取服务器进程信息
    public static List<String> processInfo(String ip){
        List<String> result = new ArrayList<>();
        TransportMapping transport = null ;
        Snmp snmp = null ;
        CommunityTarget target;
        String[] oids =
                {"1.3.6.1.2.1.25.4.2.1.1",  //index
                        "1.3.6.1.2.1.25.4.2.1.2",  //name
                        "1.3.6.1.2.1.25.4.2.1.4",  //run path
                        "1.3.6.1.2.1.25.4.2.1.6",  //type
                        "1.3.6.1.2.1.25.5.1.1.1",  //cpu
                        "1.3.6.1.2.1.25.5.1.1.2"}; //memory
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setRetries(2);
            target.setAddress(GenericAddress.parse("udp:"+ip+"/161"));
            target.setTimeout(8000);
            target.setVersion(SnmpConstants.version2c);
            TableUtils tableUtils = new TableUtils(snmp, new PDUFactory() {
                @Override
                public PDU createPDU(Target arg0) {
                    PDU request = new PDU();
                    request.setType(PDU.GET);
                    return request;
                }

                @Override
                public PDU createPDU(MessageProcessingModel messageProcessingModel) {
                    return null;
                }
            });
            OID[] columns = new OID[oids.length];
            for (int i = 0; i < oids.length; i++)
                columns[i] = new OID(oids[i]);
            @SuppressWarnings("unchecked")
            List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
            if(list.size()==1 && list.get(0).getColumns()==null){
                return result;
            }else{
                for(TableEvent event : list){
                    VariableBinding[] values = event.getColumns();
                    if(values == null) continue;
                    String name = values[1].getVariable().toString();//name
                    //String cpu = values[4].getVariable().toString();//cpu
                    //String memory = values[5].getVariable().toString();//memory
                    //String path = values[2].getVariable().toString();//path
                    result.add(name);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(transport!=null)
                    transport.close();
                if(snmp!=null)
                    snmp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    //获取端口信息
    public static List<String> portInfo(String ip){
        List<String> result = new ArrayList<>();
        TransportMapping transport = null ;
        Snmp snmp = null ;
        CommunityTarget target;
        String[] TCP_CONN = {"1.3.6.1.2.1.6.13.1.1", //status
                "1.3.6.1.2.1.6.13.1.3"}; //port
        String[] UDP_CONN = {"1.3.6.1.2.1.7.5.1.2"};
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setRetries(2);
            target.setAddress(GenericAddress.parse("udp:127.0.0.1/161"));
            target.setTimeout(8000);
            target.setVersion(SnmpConstants.version2c);
            TableUtils tableUtils = new TableUtils(snmp, new PDUFactory() {
                @Override
                public PDU createPDU(Target arg0) {
                    PDU request = new PDU();
                    request.setType(PDU.GET);
                    return request;
                }

                @Override
                public PDU createPDU(MessageProcessingModel messageProcessingModel) {
                    return null;
                }
            });
            //获取TCP 端口
            OID[] columns = new OID[TCP_CONN.length];
            for (int i = 0; i < TCP_CONN.length; i++)
                columns[i] = new OID(TCP_CONN[i]);
            @SuppressWarnings("unchecked")
            List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
            if(list.size()==1 && list.get(0).getColumns()==null){
                return result;
            }else{
                for(TableEvent event : list){
                    VariableBinding[] values = event.getColumns();
                    if(values == null) continue;
                    int status = Integer.parseInt(values[0].getVariable().toString());
                    result.add(values[1].getVariable().toString());
                }
            }
            //获取udp 端口
            OID[] udpcolumns = new OID[UDP_CONN.length];
            for (int i = 0; i < UDP_CONN.length; i++)
                udpcolumns[i] = new OID(UDP_CONN[i]);
            @SuppressWarnings("unchecked")
            List<TableEvent> udplist = tableUtils.getTable(target, udpcolumns, null, null);
            if(udplist.size()==1 && udplist.get(0).getColumns()==null){
                System.out.println(" null");
            }else{
                for(TableEvent event : udplist){
                    VariableBinding[] values = event.getColumns();
                    if(values == null) continue;
                    String name = values[0].getVariable().toString();//name
                    result.add(values[1].getVariable().toString());
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(transport!=null)
                    transport.close();
                if(snmp!=null)
                    snmp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 获取磁盘的中文名字
     * 解决snmp4j中文乱码问题
     */
    public static String getChinese(String octetString){
        if(octetString == null || "".equals(octetString)
                || "null".equalsIgnoreCase(octetString)) return "";
        try{
            String[] temps = octetString.split(":");
            if(temps.length < COLON_SIZE)
                return octetString;
            byte[] bs = new byte[temps.length];
            for(int i=0;i<temps.length;i++)
                bs[i] = (byte)Integer.parseInt(temps[i],16);
            return new String(bs,"GB2312");
        }catch(Exception e){
            return null;
        }
    }
}
