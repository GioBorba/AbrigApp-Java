package com.fiap.globalsolution.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Configuração para Abrigo
    public static final String EXCHANGE_ABRIGO = "exchange.abrigo";
    public static final String QUEUE_ABRIGO = "queue.abrigo";
    public static final String ROUTING_KEY_ABRIGO = "routingKey.abrigo";

    // Configuração para Avaliação
    public static final String EXCHANGE_AVALIACAO = "exchange.avaliacao";
    public static final String QUEUE_AVALIACAO = "queue.avaliacao";
    public static final String ROUTING_KEY_AVALIACAO = "routingKey.avaliacao";

    // Beans para Abrigo
    @Bean
    public Queue queueAbrigo() {
        return new Queue(QUEUE_ABRIGO);
    }

    @Bean
    public DirectExchange exchangeAbrigo() {
        return new DirectExchange(EXCHANGE_ABRIGO);
    }

    @Bean
    public Binding bindingAbrigo(Queue queueAbrigo, DirectExchange exchangeAbrigo) {
        return BindingBuilder.bind(queueAbrigo).to(exchangeAbrigo).with(ROUTING_KEY_ABRIGO);
    }

    // Beans para Avaliação
    @Bean
    public Queue queueAvaliacao() {
        return new Queue(QUEUE_AVALIACAO);
    }

    @Bean
    public DirectExchange exchangeAvaliacao() {
        return new DirectExchange(EXCHANGE_AVALIACAO);
    }

    @Bean
    public Binding bindingAvaliacao(Queue queueAvaliacao, DirectExchange exchangeAvaliacao) {
        return BindingBuilder.bind(queueAvaliacao).to(exchangeAvaliacao).with(ROUTING_KEY_AVALIACAO);
    }
}
