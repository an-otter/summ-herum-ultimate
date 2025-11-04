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


     // Braucht Name, Breiten- und Längengrad mit der Syntax zB 13.55657
    @PostMapping("/createLocation")
    public Travellocation createLocation(@RequestParam String name,
                                         @RequestParam Double latitude,
                                         @RequestParam Double longitude) {

            Travellocation newLocation = new Travellocation();
        newLocation.setName(name);
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        return travellocationRepository.save(newLocation);
    }
}
