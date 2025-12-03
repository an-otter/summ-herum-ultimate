package bienchen.summherum.controllers;

import bienchen.summherum.datatransferobjects.NominatimMapResponse;
import bienchen.summherum.datatransferobjects.PexelsResponse;
import bienchen.summherum.entities.Travellocation;
import bienchen.summherum.entities.User;
import bienchen.summherum.repositories.TravellocationRepository;
import bienchen.summherum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Controller
public class PageController {

    // ==========================================
    // 1. REPOSITORIES & TOOLS
    // ==========================================
    @Autowired private TravellocationRepository travellocationRepository;
    @Autowired private UserRepository userRepository; // <-- NEU: Für die User

    @Value("${api.pexels.key}") private String pexelsApiKey;
    private final WebClient webClient = WebClient.builder().build();


    // ==========================================
    // 2. SEITEN ANZEIGEN (GET)
    // ==========================================

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/showlocations")
    public String showLocationsPage(Model model) {
        model.addAttribute("orteListe", travellocationRepository.findAll());
        return "showlocations";
    }

    // --- NEU: User Seite ---
    @GetMapping("/users")
    public String showUsersPage(Model model) {
        model.addAttribute("usersListe", userRepository.findAll());
        return "users";
    }


    // ==========================================
    // 3. DATEN SPEICHERN (POST)
    // ==========================================

    // --- NEU: User speichern (Kompakt) ---
    @PostMapping("/saveUser")
    public String saveUser(@RequestParam String username) {
        User newUser = new User();
        newUser.setUsername(username);
        userRepository.save(newUser);
        return "redirect:/users";
    }

    // --- Ort speichern (Deine komplexe Logik) ---
    @PostMapping("/saveLocation")
    public String saveLocationFromForm(@RequestParam Double latitude, @RequestParam Double longitude) {
        String nominatimUrl = String.format(Locale.US, "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f", latitude, longitude);

        webClient.get().uri(nominatimUrl)
                .header("User-Agent", "SummherumApp/1.0 (deine.email@beispiel.com)")
                .retrieve().bodyToMono(NominatimMapResponse.class)
                .flatMap(res -> {
                    String name = res.getDisplayName();
                    return findLocationPhoto(name).map(photo -> {
                        Travellocation loc = new Travellocation();
                        loc.setLatitude(latitude); loc.setLongitude(longitude);
                        loc.setName(name); loc.setImageUrl(photo);
                        return loc;
                    });
                })
                .flatMap(loc -> Mono.just(travellocationRepository.save(loc)))
                .block(); // Warten bis fertig

        return "redirect:/showlocations";
    }


    // ==========================================
    // 4. HILFSMETHODEN (Private)
    // ==========================================
    private Mono<String> findLocationPhoto(String queryName) {
        return webClient.get()
                .uri("https://api.pexels.com/v1/search?query=" + queryName + "&per_page=1")
                .header("Authorization", pexelsApiKey)
                .retrieve().bodyToMono(PexelsResponse.class)
                .map(res -> (res.getPhotos() != null && !res.getPhotos().isEmpty()) ? res.getPhotos().get(0).getSrc().getMedium() : null)
                .onErrorReturn("");
    }
}