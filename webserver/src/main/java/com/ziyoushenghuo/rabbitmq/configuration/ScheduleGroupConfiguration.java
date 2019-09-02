package com.ziyoushenghuo.rabbitmq.configuration;

import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.QueueConfiguration;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ScheduleGroupConfiguration extends QueueConfiguration{


    //处理schedule模块发送的团购事件

    @Bean
    public Queue scheduleGroupStartQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_GROUP_START,true,false,false);
        return queue;
    }

    @Bean
    public Binding  GroupStartQueueBinding() {
        return BindingBuilder.bind(scheduleGroupStartQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_GROUP_START);
    }


    @Bean
    public Queue scheduleGroupStopQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_GROUP_STOP,true,false,false);
        return queue;
    }

    @Bean
    public Binding  GroupStopQueueBinding() {
        return BindingBuilder.bind(scheduleGroupStopQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_GROUP_STOP);
    }

    @Bean
    public Queue scheduleGroupShowQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_GROUP_SHOW,true,false,false);
        return queue;
    }

    @Bean
    public Binding  GroupShowQueueBinding() {
        return BindingBuilder.bind(scheduleGroupShowQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_GROUP_SHOW);
    }

}
