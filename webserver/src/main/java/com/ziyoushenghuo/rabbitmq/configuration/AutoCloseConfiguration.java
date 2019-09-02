package com.ziyoushenghuo.rabbitmq.configuration;

import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.QueueConfiguration;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


import java.util.HashMap;
import java.util.Map;


/* 配置未支付订单延迟消息
  @author 王江波
  @version V1.0
*/
@Configuration
public class AutoCloseConfiguration extends QueueConfiguration{

    //未支付订单自动关闭

    @Bean
    public Queue deadLetterAutoCloseQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_CHECK_AUTOCLOSE_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_AUTOCLOSE_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterAutoCloseBinding() {
        return BindingBuilder.bind(deadLetterAutoCloseQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_AUTOCLOSE_NAME);
    }

    @Bean
    public Queue routeAutoCloseQueue() {
        Queue queue = new Queue(MQConstant.ORDER_CHECK_AUTOCLOSE_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  AutoCloseQueueBinding() {
        return BindingBuilder.bind(routeAutoCloseQueue()).to(defaultExchange()).with(MQConstant.ORDER_CHECK_AUTOCLOSE_NAME);
    }

}
