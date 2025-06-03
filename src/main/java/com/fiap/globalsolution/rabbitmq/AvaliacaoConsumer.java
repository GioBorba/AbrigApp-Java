package com.fiap.globalsolution.rabbitmq;

import com.fiap.globalsolution.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;



@Component
public class AvaliacaoConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AVALIACAO)
    public void consumirMensagem(String mensagem) {
        System.out.println("📩 Mensagem de avaliação recebida:");
        System.out.println("📝 " + mensagem);
    }

}
