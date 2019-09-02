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
public class ScheduleGiftConfiguration extends QueueConfiguration{


    //处理schedule模块发送的开奖事件

    @Bean
    public Queue scheduleGiftStartQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_GIFT_START,true,false,false);
        return queue;
    }

    @Bean
    public Binding  GiftStartQueueBinding() {
        return BindingBuilder.bind(scheduleGiftStartQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_GIFT_START);
    }


    @Bean
    public Queue scheduleGiftStopQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_GIFT_STOP,true,false,false);
        return queue;
    }

    @Bean
    public Binding  GiftStopQueueBinding() {
        return BindingBuilder.bind(scheduleGiftStopQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_GIFT_STOP);
    }

    @Bean
    public Queue scheduleGiftShowQueue() {
        Queue queue = new Queue(MQConstant.SCHEDULE_GIFT_SHOW,true,false,false);
        return queue;
    }

    @Bean
    public Binding  GiftShowQueueBinding() {
        return BindingBuilder.bind(scheduleGiftShowQueue()).to(defaultExchange()).with(MQConstant.SCHEDULE_GIFT_SHOW);
    }

}
