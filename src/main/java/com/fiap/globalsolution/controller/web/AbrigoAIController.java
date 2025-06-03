package com.fiap.globalsolution.controller.web;

import com.fiap.globalsolution.service.AbrigoAIService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AbrigoAIController {

    private final AbrigoAIService abrigoAIService;

    public AbrigoAIController(AbrigoAIService abrigoAIService) {
        this.abrigoAIService = abrigoAIService;
    }

    @GetMapping("/chatai")
    public String chatForm(Model model) {
        model.addAttribute("title", "Assistente de Abrigos para Emergências");
        model.addAttribute("question", "");
        model.addAttribute("answer", "");
        return "chatai";
    }

    @PostMapping("/chatai")
    public String askQuestion(@RequestParam String question, Model model) {
        String answer = abrigoAIService.askAboutShelter(question);
        model.addAttribute("title", "Assistente de Abrigos para Emergências");
        model.addAttribute("question", question);
        model.addAttribute("answer", answer);
        return "chatai";
    }
}