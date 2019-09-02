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
public class GiftOvertimeConfiguration extends QueueConfiguration{

    //处理礼物超时未接收消息

    @Bean
    public Queue deadLetterGiftStatusQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_CHECK_SENDGIFT_STATUS_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_CHECK_SENDGIFT_STATUS_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterGiftStatusBinding() {
        return BindingBuilder.bind(deadLetterGiftStatusQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_CHECK_SENDGIFT_STATUS_NAME);
    }

    @Bean
    public Queue routeGiftStatusQueue() {
        Queue queue = new Queue(MQConstant.ORDER_CHECK_SENDGIFT_STATUS_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  GiftStatusBinding() {
        return BindingBuilder.bind(routeGiftStatusQueue()).to(defaultExchange()).with(MQConstant.ORDER_CHECK_SENDGIFT_STATUS_NAME);
    }

}
