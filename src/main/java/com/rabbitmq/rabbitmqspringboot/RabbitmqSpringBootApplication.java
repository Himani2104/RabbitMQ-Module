package com.rabbitmq.rabbitmqspringboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitmqSpringBootApplication {

	@Value("${spring.rabbitmq.host}")
	private String HOST;

	@Value("${spring.rabbitmq.port}")
	private int PORT;

	@Value("${spring.rabbitmq.virtual-host}")
	private String VIRTUAL_HOST;

	@Value("${spring.rabbitmq.username}")
	private String USERNAME;

	@Value("${spring.rabbitmq.password}")
	private String PASSWORD;

    @Bean
	public ConnectionFactory connectionFactory()
	{
		CachingConnectionFactory connectionFactory=new CachingConnectionFactory();
		connectionFactory.setUsername(USERNAME);
		connectionFactory.setPassword(PASSWORD);
		connectionFactory.setPort(PORT);
		connectionFactory.setHost(HOST);
		connectionFactory.setVirtualHost(VIRTUAL_HOST);
		return connectionFactory;
	}

	@Bean
	public Jackson2JsonMessageConverter messageConverter()
	{
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.findAndRegisterModules();
		return new Jackson2JsonMessageConverter(objectMapper);



	}


	@Bean
	public SimpleRabbitListenerContainerFactory listenerContainerFactory()
	{
		SimpleRabbitListenerContainerFactory containerFactory=new SimpleRabbitListenerContainerFactory();
		containerFactory.setConnectionFactory(connectionFactory());
		containerFactory.setMessageConverter(messageConverter());
		containerFactory.setMaxConcurrentConsumers(10);
		containerFactory.setMaxConcurrentConsumers(5);
		containerFactory.setAutoStartup(true);
		containerFactory.setPrefetchCount(10);
		containerFactory.setDefaultRequeueRejected(false);
		return containerFactory;
	}

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqSpringBootApplication.class, args);
	}

}
