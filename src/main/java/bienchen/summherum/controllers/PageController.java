package bienchen.summherum.controllers;

import bienchen.summherum.entities.Travellocation;
import bienchen.summherum.repositories.TravellocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // <-- WICHTIG: Nicht @RestController, der ist für's Backend
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private TravellocationRepository travellocationRepository;

    /**
     * Diese Methode wird aufgerufen, wenn jemand http://localhost:8081/
     * im Browser eingibt.
     */
    @GetMapping("/")
    public String showHomePage(Model model) {

        // 1. Hole ALLE Orte aus der Datenbank
        Iterable<Travellocation> alleOrte = travellocationRepository.findAll();

        // 2. Packe diese Liste in einen "Korb" (das Model),
        //    damit die HTML-Seite sie lesen kann.
        //    Wir nennen die Liste im Korb "orteListe".
        model.addAttribute("orteListe", alleOrte);

        // 3. Gib den NAMEN der HTML-Datei zurück.
        // Spring sucht jetzt automatisch nach:
        // /src/main/resources/templates/index.html
        return "index";
    }
}