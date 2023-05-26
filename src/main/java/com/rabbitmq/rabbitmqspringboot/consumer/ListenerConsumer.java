package com.rabbitmq.rabbitmqspringboot.consumer;

import com.rabbitmq.rabbitmqspringboot.model.QueueObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ListenerConsumer {

@Autowired
    AmqpTemplate directExchange;

    private static final  String HEADER_X_RETRY="x-retry";
    private static final int MAX_RETRY=3;

    @RabbitListener(queues={"${rabbitmq.direct.queue-1}","${rabbitmq.direct.queue-2}"},
            containerFactory="listenerContainerFactory")

    public void receiveMessage(@Payload QueueObject object, Message message)
    {
        try {
            System.out.println(object);
            throw new RuntimeException();
        }
        catch (Exception e)
        {
            Integer retryCount= (Integer) message.getMessageProperties().getHeaders().get(HEADER_X_RETRY);
            if(retryCount==null)
            {
                retryCount=0;
            }
            else if (retryCount>=MAX_RETRY)
            {
                log.info("Message was Ignored");
                return;
            }
            log.info("Retrying Message For the {} time",retryCount);
            message.getMessageProperties().getHeaders().put(HEADER_X_RETRY,++retryCount);

           directExchange.send(message.getMessageProperties().getReceivedRoutingKey(),message);
        }

      //  System.out.println(object);
        //throw new RuntimeException();
    }

}
