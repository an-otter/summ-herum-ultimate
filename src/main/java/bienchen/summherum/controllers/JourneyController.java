package bienchen.summherum.controllers;

import bienchen.summherum.entities.Journey;
import bienchen.summherum.repositories.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class JourneyController {

    @Autowired
    private JourneyRepository journeyRepository;

    @GetMapping("/journey1") //http://localhost:8081/journey1
    public Journey getJourney1(){
        return journeyRepository.findById(1L).orElseThrow();
    }
}
