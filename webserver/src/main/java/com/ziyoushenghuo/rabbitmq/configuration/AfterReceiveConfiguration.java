package com.ziyoushenghuo.rabbitmq.configuration;

import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.QueueConfiguration;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


import java.util.HashMap;
import java.util.Map;


/* 配置收货后延迟消息
  @author 王江波
  @version V1.0
*/
@Configuration
public class AfterReceiveConfiguration extends QueueConfiguration{

    //信道配置

    @Bean
    public Queue deadLetterAfterReceiveQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_CHECK_AFTERRECEIVE_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_AFTERRECEIVE_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterAfterReceiveBinding() {
        return BindingBuilder.bind(deadLetterAfterReceiveQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_AFTERRECEIVE_NAME);
    }


    @Bean
    public Queue routeAfterReceiveQueue() {
        Queue queue = new Queue(MQConstant.ORDER_CHECK_AFTERRECEIVE_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  AfterReceiveQueueBinding() {
        return BindingBuilder.bind(routeAfterReceiveQueue()).to(defaultExchange()).with(MQConstant.ORDER_CHECK_AFTERRECEIVE_NAME);
    }



}
