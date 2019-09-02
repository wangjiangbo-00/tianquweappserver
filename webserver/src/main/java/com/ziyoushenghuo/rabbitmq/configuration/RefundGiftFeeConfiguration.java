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
public class RefundGiftFeeConfiguration extends QueueConfiguration{

    //配置收礼后延迟退还礼品预售运费

    @Bean
    public Queue deadLetterRefundGiftShipFeeQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_DO_REFUND_GIIF_SHIPFEE_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_DO_REFUND_GIIF_SHIPFEE_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterRefundGiftShipFeeBinding() {
        return BindingBuilder.bind(deadLetterRefundGiftShipFeeQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_DO_REFUND_GIIF_SHIPFEE_NAME);
    }

    @Bean
    public Queue routeRefundGiftShipFeeQueue() {
        Queue queue = new Queue(MQConstant.ORDER_DO_REFUND_GIIF_SHIPFEE_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  RefundGiftShipFeeQueueBinding() {
        return BindingBuilder.bind(routeRefundGiftShipFeeQueue()).to(defaultExchange()).with(MQConstant.ORDER_DO_REFUND_GIIF_SHIPFEE_NAME);
    }

}
