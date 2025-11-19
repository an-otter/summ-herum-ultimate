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

    @GetMapping("/showlocations")
    public String showLocationsPage(Model model) {
        Iterable<Travellocation> alleOrte = travellocationRepository.findAll();
        model.addAttribute("orteListe", alleOrte);
        return "showlocations";
    }
}