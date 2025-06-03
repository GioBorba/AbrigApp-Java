package com.fiap.globalsolution.service;


import org.springframework.ai.chat.ChatClient;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AbrigoAIService {

    private final ChatClient chatClient;

    @Value("${spring.ai.openai.chat.options.model:gpt-4}")
    private String model;

    public AbrigoAIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String askAboutShelter(String question) {
        try {
            if (question == null || question.trim().isEmpty()) {
                return "Por favor, descreva sua necessidade relacionada a abrigos para eventos extremos.";
            }

            String promptText = """
            Você é um especialista em gestão de abrigos para eventos extremos.
            Responda com informações precisas sobre:
            - Montagem de abrigos temporários
            - Protocolos de emergência
            - Distribuição de recursos
            - Coordenação com defesa civil
            
            Pergunta: {question}
            Resposta:""";

            PromptTemplate promptTemplate = new PromptTemplate(promptText);
            Prompt prompt = promptTemplate.create(Map.of("question", question));

            // Método atualizado - usar generate() em vez de call()
            return chatClient.call(prompt).getResult().getOutput().getContent();

        } catch (Exception e) {
            return "Desculpe, serviço indisponível. Contate a Defesa Civil (199). Erro: " + e.getMessage();
        }
    }
}