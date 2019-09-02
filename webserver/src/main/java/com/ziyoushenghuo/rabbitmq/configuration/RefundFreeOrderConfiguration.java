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
public class RefundFreeOrderConfiguration extends QueueConfiguration{

    //判断收礼返现后延迟退款

    @Bean
    public Queue deadLetterRefundFreeOrderQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_DO_REFUND_FREE_ORDER_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_DO_REFUND_FREE_ORDER_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterRefundFreeOrderBinding() {
        return BindingBuilder.bind(deadLetterRefundFreeOrderQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_DO_REFUND_FREE_ORDER_NAME);
    }

    @Bean
    public Queue routeRefundFreeOrderQueue() {
        Queue queue = new Queue(MQConstant.ORDER_DO_REFUND_FREE_ORDER_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  RefundFreeOrderQueueBinding() {
        return BindingBuilder.bind(routeRefundFreeOrderQueue()).to(defaultExchange()).with(MQConstant.ORDER_DO_REFUND_FREE_ORDER_NAME);
    }

}
