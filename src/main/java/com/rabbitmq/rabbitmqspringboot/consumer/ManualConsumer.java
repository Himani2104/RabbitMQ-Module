package com.rabbitmq.rabbitmqspringboot.consumer;

import com.rabbitmq.rabbitmqspringboot.model.QueueObject;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
public class ManualConsumer {

    @Autowired
   private AmqpAdmin amqpAdmin;

    @Autowired
   private RabbitTemplate rabbitTemplate;


    private int getCountOfMessage(String queueName)
    {
        Properties properties=amqpAdmin.getQueueProperties(queueName);
        return (int) properties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
    }

    public List<QueueObject> receiveMessage(String queueName)
    {
        int count=getCountOfMessage(queueName);
        return IntStream.range(0,count).mapToObj(i->(QueueObject) rabbitTemplate.receiveAndConvert(queueName))
                .collect(Collectors.toList());
    }


}
