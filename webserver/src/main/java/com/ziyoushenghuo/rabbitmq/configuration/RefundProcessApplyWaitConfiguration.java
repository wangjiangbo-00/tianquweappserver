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
public class RefundProcessApplyWaitConfiguration extends QueueConfiguration{

    //卖家回复超时等待

    @Bean
    public Queue deadLetterRefundApplyWaitQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_REFUND_REPLY_WAIT_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_REFUND_REPLY_WAIT_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterRefundApplyWaitBinding() {
        return BindingBuilder.bind(deadLetterRefundApplyWaitQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_REFUND_REPLY_WAIT_NAME);
    }

    @Bean
    public Queue routeRefundApplyWaitQueue() {
        Queue queue = new Queue(MQConstant.ORDER_REFUND_REPLY_WAIT_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  RefundApplyWaitBinding() {
        return BindingBuilder.bind(routeRefundApplyWaitQueue()).to(defaultExchange()).with(MQConstant.ORDER_REFUND_REPLY_WAIT_NAME);
    }

}
