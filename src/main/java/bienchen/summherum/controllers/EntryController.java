package bienchen.summherum.controllers;
import bienchen.summherum.entities.Entry;
import bienchen.summherum.entities.Journey;
import bienchen.summherum.entities.Travellocation;
import bienchen.summherum.repositories.EntryRepository;
import bienchen.summherum.repositories.JourneyRepository;
import bienchen.summherum.repositories.TravellocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.Instant;

@RestController
public class EntryController {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private TravellocationRepository travellocationRepository;

    @GetMapping("/entry1")
    public Entry getEntry1() {
        return entryRepository.findById(1L).orElseThrow();
    }

    @PostMapping("/createEntry")
    public Entry createEntry(@RequestParam String title,
                             @RequestParam String text,
                             @RequestParam Long journeyId,
                             @RequestParam Long locationId) {

        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new RuntimeException("Journey not found with id: " + journeyId));
        Travellocation location = travellocationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));

        Entry newEntry = new Entry();
        newEntry.setTitle(title);
        newEntry.setText(text);
        newEntry.setTimestamp(Instant.now());
        newEntry.setJourney(journey);
        newEntry.setLocation(location);
        return entryRepository.save(newEntry);
    }
}