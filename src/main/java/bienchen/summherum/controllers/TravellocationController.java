package bienchen.summherum.controllers;

import bienchen.summherum.entities.Travellocation;
import bienchen.summherum.repositories.TravellocationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import bienchen.summherum.datatransferobjects.NominatimMapResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Locale;

@RestController
public class TravellocationController {

    @Autowired
    private TravellocationRepository travellocationRepository;

    // Initialisiert den WebClient
    private final WebClient webClient = WebClient.builder().build();

    // berechnet automatisch aus longitude und langitude den realen Ort als Textausgabe
    @PostMapping("/createLocationAuto")
    public Mono<Travellocation> createLocationAuto(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        // Baut die API-URL für Nominatim - die OpenSource Weltkarte
        String url = String.format(
                Locale.US, // muss sein, um kein Error 400 zu kriegen
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f",
                latitude, longitude
        );

        // Ruft die API auf
        return webClient.get()
                .uri(url)
                // Nominatim verlangt einen "User-Agent" mit realer Email
                .header("User-Agent", "SummherumApp/1.0 (heike.ademmer@th-brandenburg.de)")
                .retrieve() // Starte den Abruf
                .bodyToMono(NominatimMapResponse.class) // Wandle die Antwort in unser DTO um
                .flatMap(nominatimResponse -> {
                    // 3. WENN der Abruf erfolgreich war:
                    String locationName = nominatimResponse.getDisplayName();
                        Travellocation newLocation = new Travellocation();
                    newLocation.setLatitude(latitude);
                    newLocation.setLongitude(longitude);
                    newLocation.setName(locationName); // dieser kommt von der API!
                    return Mono.just(travellocationRepository.save(newLocation));
                });
    }

    @GetMapping("/travellocation1")
    public Travellocation getTravellocation1() {
        return travellocationRepository.findById(1L).orElseThrow();
    }


     // Braucht Name, Breiten- und Längengrad mit der Syntax zB 13.55657; wird eigentlich durch LocationAuto hinfällig
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
