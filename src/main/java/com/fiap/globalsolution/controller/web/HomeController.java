package com.fiap.globalsolution.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model, Locale locale) {
        model.addAttribute("appName", "AbrigApp");
        return "home";
    }
}
