package bienchen.summherum.controllers;

import bienchen.summherum.entities.Journey;
import bienchen.summherum.repositories.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JourneyController {

    @Autowired
    private JourneyRepository journeyRepository;

    @GetMapping("/journey1")
    public Journey getJourney1(){
        return journeyRepository.findById(1L).orElseThrow();
    }
}
