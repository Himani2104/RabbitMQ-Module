package com.rabbitmq.rabbitmqspringboot.controller;

import com.rabbitmq.rabbitmqspringboot.consumer.ManualConsumer;
import com.rabbitmq.rabbitmqspringboot.model.QueueObject;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class Controller {

    @Value("${rabbitmq.direct.routing-key-1}")
    private String DIRECT_ROUTING_KEY_1;

    @Value("${rabbitmq.direct.routing-key-2}")
    private String DIRECT_ROUTING_KEY_2;

    @Autowired
    private AmqpTemplate defaultExchange;

    @Autowired
    private AmqpTemplate directExchange;

    @Autowired
    private AmqpTemplate fanoutExchange;

    @Autowired
    private AmqpTemplate topicExchange;


    @Autowired
    private AmqpTemplate headerExchange;

    @Autowired
    private ManualConsumer manualConsumer;



    @PostMapping("/message")
    public ResponseEntity<?> sendMessageWithDefaultExchange()
    {
        QueueObject object=new QueueObject("default", LocalDateTime.now());
        defaultExchange.convertAndSend(object);
        return ResponseEntity.ok("Send Message with Default Exchange");
    }

    @PostMapping("/direct/{key}")
    public ResponseEntity<?> sendMessageWithDirectExchange(@PathVariable int key)
    {
    QueueObject queueObject=new QueueObject("direct",LocalDateTime.now());
    String routingKey = key == 1 ? DIRECT_ROUTING_KEY_1 : DIRECT_ROUTING_KEY_2;

     directExchange.convertAndSend(routingKey,queueObject);
     return ResponseEntity.ok("Direct Exchange");
    }

    @PostMapping("/fanout")
    public ResponseEntity<?> sendMessageWithfanoutExchange()
    {
        QueueObject object=new QueueObject("fanout", LocalDateTime.now());
        fanoutExchange.convertAndSend(object);
        return ResponseEntity.ok("Send Message with Fanout Exchange");
    }

    @PostMapping("/topic/{key}")

    public  ResponseEntity<?> sendMessageWithTopicExchange(@PathVariable String key)
    {
        QueueObject object=new QueueObject("topic",LocalDateTime.now());
        topicExchange.convertAndSend(key,object);
        return ResponseEntity.ok("Send Message using Topic Exchange");
    }

    @PostMapping("/header")
    public ResponseEntity<?> sendMessageWithHeaderExchange(@RequestParam(value = "error",required = false) String error,
                                                           @RequestParam(value = "debug",required = false) String debug,
                                                           @RequestParam(value = "warning",required = false) String warning,
                                                           @RequestParam(value = "info",required = false) String info)
    {
        QueueObject object=new QueueObject("header",LocalDateTime.now());
        MessageBuilder builder=MessageBuilder.withBody(object.toString().getBytes());
        if (error!=null)
        {
            builder.setHeader("error",error);
        }
        if (debug!=null)
        {
            builder.setHeader("debug",debug);
        }
        if (warning!=null)
        {
            builder.setHeader("warning",warning);
        }

        if (info!=null)
        {
            builder.setHeader("info",info);
        }
        headerExchange.convertAndSend(builder.build());

        return ResponseEntity.ok("Header Exchange");
    }

    @PostMapping("/consumer/{queueName}")

    public ResponseEntity<?> consumeAllMessagesOfTheQueue(@PathVariable String queueName)
    {
        return new ResponseEntity<>(manualConsumer.receiveMessage(queueName), HttpStatus.OK);
    }

}

