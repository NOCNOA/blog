package com.example.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin-ui")
    public String admin() {
        return "admin";
    }

    @GetMapping("/post/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        model.addAttribute("articleId", id);
        return "article";
    }

    @GetMapping("/admin-ui/editor")
    public String editorNew(Model model) {
        model.addAttribute("articleId", "");
        return "editor";
    }

    @GetMapping("/admin-ui/editor/{id}")
    public String editorEdit(@PathVariable Long id, Model model) {
        model.addAttribute("articleId", id);
        return "editor";
    }
}
