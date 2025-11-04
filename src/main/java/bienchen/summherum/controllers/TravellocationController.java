package bienchen.summherum.controllers;

import bienchen.summherum.entities.Travellocation;
import bienchen.summherum.repositories.TravellocationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TravellocationController {

    @Autowired
    private TravellocationRepository travellocationRepository;

    @GetMapping("/travellocation1")
    public Travellocation getTravellocation1() {
        return travellocationRepository.findById(1L).orElseThrow();
    }

    // Hier können wir gleich dein @PostMapping hinzufügen!
}
