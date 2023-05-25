package com.rabbitmq.rabbitmqspringboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class QueueObject {
    private String type;
    private LocalDateTime time;
}
