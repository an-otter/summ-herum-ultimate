package bienchen.summherum.controllers;

import bienchen.summherum.entities.Entry;
import bienchen.summherum.repositories.EntryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class EntryController {

    @Autowired
    private EntryRepository entryRepository;

    @GetMapping("/entry1")
    public Entry getEntry1() {
        return entryRepository.findById(1L).orElseThrow();
    }

    // Hier können wir gleich dein @PostMapping hinzufügen!
}