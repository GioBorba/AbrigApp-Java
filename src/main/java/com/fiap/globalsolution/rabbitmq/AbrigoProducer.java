package com.fiap.globalsolution.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.fiap.globalsolution.config.RabbitMQConfig.*;

@Component
public class AbrigoProducer {

    private final RabbitTemplate rabbitTemplate;

    public AbrigoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarMensagem(String mensagem) {
        rabbitTemplate.convertAndSend(EXCHANGE_ABRIGO, ROUTING_KEY_ABRIGO, mensagem);
    }
}

