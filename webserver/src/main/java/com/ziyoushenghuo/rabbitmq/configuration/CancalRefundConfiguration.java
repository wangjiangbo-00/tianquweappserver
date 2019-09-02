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
public class CancalRefundConfiguration extends QueueConfiguration{

    //处理订单未发货，用户取消时的流程

    @Bean
    public Queue deadLetterAfterCancalRefundQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_CHECK_CANCALREFUND_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_CANCALREFUND_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterAfterDoRefundBinding() {
        return BindingBuilder.bind(deadLetterAfterCancalRefundQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_CANCALREFUND_NAME);
    }



    @Bean
    public Queue routeAfterCancalRefundQueue() {
        Queue queue = new Queue(MQConstant.ORDER_CHECK_CANCALREFUND_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  AfterDoRefundQueueBinding() {
        return BindingBuilder.bind(routeAfterCancalRefundQueue()).to(defaultExchange()).with(MQConstant.ORDER_CHECK_CANCALREFUND_NAME);
    }

}
