package com.ziyoushenghuo.rabbitmq.delay;



import com.ziyoushenghuo.common.JSONUtils;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DelayUnPaySender {

	@Autowired
	private AmqpTemplate rabbitTemplate;


	public void send(String msg, long times) {

		MessagePostProcessor processor = new MessagePostProcessor(){
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				message.getMessageProperties().setExpiration(times + "");
				return message;
			}
		};

		rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE,MQConstant.DEAD_LETTER_QUEUE_UNPAY_NAME, msg, processor);
	}
	public void send(String msg) {
		send(msg,MQConstant.ORDER_CHECK_UNPAY_TIME);
	}


}