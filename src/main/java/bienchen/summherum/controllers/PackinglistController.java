package bienchen.summherum.controllers;

import bienchen.summherum.entities.Journey;
import bienchen.summherum.entities.Packinglist;
import bienchen.summherum.repositories.JourneyRepository;
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

    @Autowired
    private JourneyRepository journeyRepository;

    @GetMapping("/packinglist1")
    public Packinglist getPackinglist1() {
        return packinglistRepository.findById(1L).orElseThrow();
    }

    @PostMapping("/createPackinglist")
    public Packinglist createPackinglist(@RequestParam String name,
                                         @RequestParam String season,
                                         @RequestParam String itemsText,
                                         @RequestParam Long journeyId) {

        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new RuntimeException("Journey not found with id: " + journeyId));

        Packinglist newList = new Packinglist();
        newList.setName(name);
        newList.setSeason(season);
        newList.setItemsText(itemsText);
        newList.setJourney(journey);
        return packinglistRepository.save(newList);
    }
}
