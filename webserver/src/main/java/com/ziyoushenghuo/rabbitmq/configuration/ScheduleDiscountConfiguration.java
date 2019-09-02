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
public class ScheduleDiscountConfiguration extends QueueConfiguration{

    //处理schedule模块发送的折扣事件

    @Bean
    public Queue scheduleDiscountStartQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_DISCOUNT_START,true,false,false);
        return queue;
    }

    @Bean
    public Binding  DiscountStartQueueBinding() {
        return BindingBuilder.bind(scheduleDiscountStartQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_DISCOUNT_START);
    }


    @Bean
    public Queue scheduleDiscountStopQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_DISCOUNT_STOP,true,false,false);
        return queue;
    }

    @Bean
    public Binding  DiscountStopQueueBinding() {
        return BindingBuilder.bind(scheduleDiscountStopQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_DISCOUNT_STOP);
    }

    @Bean
    public Queue scheduleDiscountShowQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_DISCOUNT_SHOW,true,false,false);
        return queue;
    }

    @Bean
    public Binding  DiscountShowQueueBinding() {
        return BindingBuilder.bind(scheduleDiscountShowQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_DISCOUNT_SHOW);
    }

}
