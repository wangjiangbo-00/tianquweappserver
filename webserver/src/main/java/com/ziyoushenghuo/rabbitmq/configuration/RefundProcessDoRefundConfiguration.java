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
public class RefundProcessDoRefundConfiguration extends QueueConfiguration{

    //最终发起退款配置

    @Bean
    public Queue deadLetterRefundSettleQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_REFUND_OK_SETTILE_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_REFUND_OK_SETTILE_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterRefundSettleBinding() {
        return BindingBuilder.bind(deadLetterRefundSettleQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_REFUND_OK_SETTILE_NAME);
    }

    @Bean
    public Queue routeRefundSettleQueue() {
        Queue queue = new Queue(MQConstant.ORDER_REFUND_OK_SETTILE_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  RefundSettleBinding() {
        return BindingBuilder.bind(routeRefundSettleQueue()).to(defaultExchange()).with(MQConstant.ORDER_REFUND_OK_SETTILE_NAME);
    }

}
