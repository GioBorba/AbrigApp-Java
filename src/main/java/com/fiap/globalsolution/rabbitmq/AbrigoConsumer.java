package com.fiap.globalsolution.rabbitmq;

import com.fiap.globalsolution.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class AbrigoConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ABRIGO)
    public void consumirMensagem(String mensagem) {
        System.out.println("📩 Mensagem de abrigo recebida:");
        System.out.println("📝 " + mensagem);
    }

}
