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
public class UngroupConfiguration extends QueueConfiguration{

    //处理个人开团超期事件

    @Bean
    public Queue deadLetterUnGroupQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_CHECK_UNGROUP_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_UNGROUP_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterUnGroupBinding() {
        return BindingBuilder.bind(deadLetterUnGroupQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_UNGROUP_NAME);
    }

    @Bean
    public Queue routeUnGroupQueue() {
        Queue queue = new Queue(MQConstant.ORDER_CHECK_UNGROUP_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  UnGroupQueueBinding() {
        return BindingBuilder.bind(routeUnGroupQueue()).to(defaultExchange()).with(MQConstant.ORDER_CHECK_UNGROUP_NAME);
    }

}
