package bienchen.summherum.controllers;

import bienchen.summherum.entities.Packinglist;
import bienchen.summherum.repositories.PackinglistRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class PackinglistController {

    @Autowired
    private PackinglistRepository packinglistRepository;

    @GetMapping("/packinglist1")
    public Packinglist getPackinglist1() {
        return packinglistRepository.findById(1L).orElseThrow();
    }

    // Hier können wir gleich dein @PostMapping hinzufügen!
}
