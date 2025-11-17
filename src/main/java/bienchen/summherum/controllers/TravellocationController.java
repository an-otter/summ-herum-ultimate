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
import bienchen.summherum.datatransferobjects.PexelsResponse;
import org.springframework.beans.factory.annotation.Value;
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
                    // 3. WENN der Abruf erfolgreich war, kriegen wir den Location Namen:
                    String locationName = nominatimResponse.getDisplayName();
                    //Pexels Foto
                    return findLocationPhoto(locationName)
                            .map(photoUrl -> {
                                // SCHRITT 3: Baue das fertige Objekt
                                Travellocation newLocation = new Travellocation();
                                newLocation.setLatitude(latitude);
                                newLocation.setLongitude(longitude);
                                newLocation.setName(locationName); // <-- von NomatimMap
                                newLocation.setImageUrl(photoUrl);  // <-- von PexelPics
                                return newLocation;
                            });
                })
                .flatMap(newLocation -> {
                    // SCHRITT 4: Speichere das fertige Objekt in der DB
                    return Mono.just(travellocationRepository.save(newLocation));
                });
    }

    // normaler Get Aufruf, um zu sehen was die DB gespeichert hat
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

        //Pexel Logik Werkzeuge
        // Holt den API-Schlüssel aus application.yml
        @Value("${api.pexels.key}")
        private String pexelsApiKey;

        // Hilfs-Methode, die Pexels nach einem Foto durchsucht
        // Wird oben von createLocationAuto aufgerufen
        private Mono<String> findLocationPhoto(String queryName) {
        // Baut die Pexels-API-URL
        String pexelsUrl = "https://api.pexels.com/v1/search?query=" + queryName + "&per_page=1";

        return webClient.get()
                .uri(pexelsUrl)
                // Sendet den API-Schlüssel im Header mit
                .header("Authorization", pexelsApiKey)
                .retrieve()
                .bodyToMono(PexelsResponse.class) // Benutzt dein Pexels-DTO
                .map(response -> {
                    // Prüft, ob Fotos gefunden wurden
                    if (response.getPhotos() != null && !response.getPhotos().isEmpty()) {
                        // Gibt die "medium" URL des ersten Fotos zurück
                        return response.getPhotos().get(0).getSrc().getMedium();
                    }
                    return null; // Kein Foto gefunden
                })
                // WICHTIG: Wenn Pexels fehlschlägt, stürzt die App nicht ab.
                .onErrorReturn("");
    }
}













