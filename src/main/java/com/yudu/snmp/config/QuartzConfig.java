package com.yudu.snmp.config;
import com.yudu.snmp.job.SnmpTask;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {
    //定时任务1
    @Bean(name = "firstJob")
    public MethodInvokingJobDetailFactoryBean firstJobDetail(SnmpTask snmpTask){
        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
        // 是否并发执行
        jobDetail.setConcurrent(false);
        // 需要执行的对象
        jobDetail.setTargetObject(snmpTask);
        //需要执行的方法
        jobDetail.setTargetMethod("snmp");
        return jobDetail;
    }

    // 触发器1
    @Bean(name = "firstTrigger")
    public CronTriggerFactoryBean firstTrigger(JobDetail firstJob){
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(firstJob);
        //cron表达式，每天6点到23点每1分钟执行一次(从左到右，秒 分 小时 天 月 星期 年) 0 0/30 8-22 * * ? *
        trigger.setCronExpression("0 0/1 6-23 * * ? *");
        return trigger;
    }

    // 调度工厂
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactory(Trigger firstTrigger) {

        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        // 延时启动，应用启动1秒后
        bean.setStartupDelay(1);
        //注册触发器
        bean.setTriggers(firstTrigger);

        return bean;
    }
}
