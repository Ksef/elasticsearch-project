package com.example.elasticsearchproject.controllers;

import com.example.elasticsearchproject.models.Article;
import com.example.elasticsearchproject.services.EsService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final EsService esService;

    @SneakyThrows
    @PutMapping("/articles")
    public String addArticle(@RequestParam("title") String title,
                             @RequestParam("text") String text) {
        String id = UUID.randomUUID().toString();
        esService.updateArticle(id, title, text);
        return id;
    }

    @GetMapping("/search")
    public List<Article> search(@RequestParam("query") String query) {
        return esService.search(query);
    }
}
