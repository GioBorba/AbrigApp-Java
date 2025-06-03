package com.fiap.globalsolution.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.fiap.globalsolution.config.RabbitMQConfig.*;


@Component
public class AvaliacaoProducer {

    private final RabbitTemplate rabbitTemplate;

    public AvaliacaoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarMensagem(String mensagem) {
        rabbitTemplate.convertAndSend(EXCHANGE_AVALIACAO, ROUTING_KEY_AVALIACAO, mensagem);
    }
}
