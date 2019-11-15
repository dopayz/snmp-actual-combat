package com.yudu.snmp.repository;

import com.yudu.snmp.pojo.SmsOut;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SmsOutRepository {
    @Select("select * from SMSOUT_ where isVisible = 0 and isSuccess= 0")
    @Results({
            @Result(property = "id",  column = "id"),
            @Result(property = "rNum", column = "rNum"),
            @Result(property = "rPerson", column = "rPerson"),
            @Result(property = "rPersonId", column = "rPersonId"),
            @Result(property = "context", column = "context"),
            @Result(property = "date", column = "date"),
            @Result(property = "isSuccess", column = "isSuccess"),
            @Result(property = "type", column = "type"),
            @Result(property = "isVisible", column = "isVisible"),
    })
    //获取没发送的短信
    List<SmsOut> getAllNoIsVisible();

    @Insert("insert into SMSOUT_(id,rNum,rPerson,rPersonId,context,date,isSuccess,type,isVisible) VALUES(#{id},#{rNum},#{rPerson},#{rPersonId},#{context},#{date},#{isSuccess},#{type},#{isVisible})")
    void insert(SmsOut smsOut);
}
