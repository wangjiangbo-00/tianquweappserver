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
public class RefundProcessAutoReceiveConfiguration extends QueueConfiguration{

    //卖家超时自动收货并退款

    @Bean
    public Queue deadLetterRefundReceiveWaitQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_CHECK_REFUND_AUTO_RECEIVE_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_REFUND_AUTO_RECEIVE_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterRefundReceiveBinding() {
        return BindingBuilder.bind(deadLetterRefundReceiveWaitQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_REFUND_AUTO_RECEIVE_NAME);
    }

    @Bean
    public Queue routeRefundReceiveQueue() {
        Queue queue = new Queue(MQConstant.ORDER_CHECK_REFUND_AUTO_RECEIVE_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  RefundReceiveBinding() {
        return BindingBuilder.bind(routeRefundReceiveQueue()).to(defaultExchange()).with(MQConstant.ORDER_CHECK_REFUND_AUTO_RECEIVE_NAME);
    }

}
