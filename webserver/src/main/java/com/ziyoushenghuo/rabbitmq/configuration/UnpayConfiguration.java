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
public class UnpayConfiguration extends QueueConfiguration{

    //处理超期未支付事件

    @Bean
    public Queue deadLetterUnPayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_CHECK_UNPAY_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_UNPAY_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterUnPayBinding() {
        return BindingBuilder.bind(deadLetterUnPayQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_UNPAY_NAME);
    }

    @Bean
    public Queue routeUnPayQueue() {
        Queue queue = new Queue(MQConstant.ORDER_CHECK_UNPAY_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  UnPayQueueBinding() {
        return BindingBuilder.bind(routeUnPayQueue()).to(defaultExchange()).with(MQConstant.ORDER_CHECK_UNPAY_NAME);
    }

}
