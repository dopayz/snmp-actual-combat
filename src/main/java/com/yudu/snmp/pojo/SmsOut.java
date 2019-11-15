package com.yudu.snmp.pojo;

import java.io.Serializable;
import java.util.Date;

public class SmsOut implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final int KQ = 1;
   public static final int GW = 2;
   public static final int TZGG = 3;

   /** 主键 */
   private String id;

   /** 接收人号�?*/
   private String rNum;

   /** 接收人姓�? 不在通讯录中则写�?*/
   private String rPerson;

   /** 接收人ID，不在�?讯录中则填null */
   private String rPersonId;

   private String context;// 内容

   private Date date;// 发�?时间

   // 是否成功0.账号不存在，1.已发�?
   private Integer isSuccess;

   /**
    * 1,考勤 2,公文 3,通知公告 4.回复短信 5.收文发送
    */
   private Integer type;

   private Boolean isVisible;// 是否可见

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getrNum() {
      return rNum;
   }

   public void setrNum(String rNum) {
      this.rNum = rNum;
   }

   public String getrPerson() {
      return rPerson;
   }

   public void setrPerson(String rPerson) {
      this.rPerson = rPerson;
   }

   public String getrPersonId() {
      return rPersonId;
   }

   public void setrPersonId(String rPersonId) {
      this.rPersonId = rPersonId;
   }

   public String getContext() {
      return context;
   }

   public void setContext(String context) {
      this.context = context;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public Integer getIsSuccess() {
      return isSuccess;
   }

   public void setIsSuccess(Integer isSuccess) {
      this.isSuccess = isSuccess;
   }

   public Integer getType() {
      return type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public Boolean getIsVisible() {
      return isVisible;
   }

   public void setIsVisible(Boolean isVisible) {
      this.isVisible = isVisible;
   }

}