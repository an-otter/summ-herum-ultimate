package bienchen.summherum.controllers;

import bienchen.summherum.datatransferobjects.NominatimMapResponse;
import bienchen.summherum.datatransferobjects.PexelsResponse;
import bienchen.summherum.datatransferobjects.MeteoResponse;
import bienchen.summherum.entities.Travellocation;
import bienchen.summherum.entities.User;
import bienchen.summherum.repositories.JourneyRepository;
import bienchen.summherum.repositories.TravellocationRepository;
import bienchen.summherum.repositories.UserRepository;
import bienchen.summherum.repositories.EntryRepository;
import bienchen.summherum.repositories.PackinglistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Sort;
import java.util.List;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;

import java.util.Locale;

@Controller
public class PageController {

    // ==========================================
    // 1. REPOSITORIES & TOOLS
    // ==========================================
    @Autowired private TravellocationRepository travellocationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JourneyRepository journeyRepository;
    @Autowired private EntryRepository entryRepository;
    @Autowired private PackinglistRepository packinglistRepository;

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

    @GetMapping("/locations")
    public String showNewLocationPage() {
        // Zeigt einfach nur die locations.html (das Formular) an
        return "locations";
    }

    @GetMapping("/users")
    public String showUsersPage(Model model) {
        model.addAttribute("usersListe", userRepository.findAll());
        return "users";
    }

    @GetMapping("/timeline")
    public String showTimelinePage(Model model) {
        // Wir holen die Einträge, aber sortiert!
        // "timestamp" ist der Name des Feldes in deiner Entry.java
        // DESC = Descending (Absteigend -> Neuestes zuerst)
        List<bienchen.summherum.entities.Entry> sortedEntries =
                entryRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));

        model.addAttribute("timelineList", sortedEntries);

        return "timeline";
    }

    // ==========================================
    // 3. DATEN SPEICHERN (POST)
    // ==========================================

    // --- NEU: User speichern (Kompakt) ---
    @PostMapping("/saveUser")
    public String saveUser(@RequestParam String username,
                           @RequestParam String password) { // <-- NEU

        bienchen.summherum.entities.User newUser = new bienchen.summherum.entities.User();
        newUser.setUsername(username);
        newUser.setPassword(password); // <-- NEU: Passwort setzen

        userRepository.save(newUser);
        return "redirect:/users";
    }

    // ==========================================
// --- LOGIN & SESSION SYSTEM (Feature 4) ---
// ==========================================

    // 1. Login Seite anzeigen
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 2. Login durchführen (Daten prüfen)
    @PostMapping("/performLogin")
    public String performLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,    // <-- Der Rucksack des Users
                               Model model) {

        // Suche User in der DB
        // (Achtung: findByUsername musst du evtl. im Repository ergänzen, siehe unten!)
        // Wir machen es hier "quick & dirty" mit einer Schleife, falls du das Repo nicht ändern willst:

        bienchen.summherum.entities.User foundUser = null;
        for (bienchen.summherum.entities.User u : userRepository.findAll()) {
            if (u.getUsername().equals(username)) {
                foundUser = u;
                break;
            }
        }

        // Prüfung: User gefunden UND Passwort stimmt?
        if (foundUser != null && foundUser.getPassword().equals(password)) {
            // JA! -> Ab in den Rucksack (Session)
            session.setAttribute("loggedInUser", foundUser);
            return "redirect:/"; // Zurück zur Startseite
        } else {
            // NEIN! -> Fehler anzeigen
            model.addAttribute("error", true);
            return "login";
        }
    }

    // 3. Ausloggen
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Rucksack ausleeren
        return "redirect:/";
    }

    // --- Ort speichern ---
    @PostMapping("/saveLocation")
    public String saveLocationFromForm(@RequestParam Double latitude, @RequestParam Double longitude) {
        String nominatimUrl = String.format(Locale.US, "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f", latitude, longitude);

        webClient.get().uri(nominatimUrl)
                .header("User-Agent", "SummherumApp/1.0 (heike.ademmer@th-brandenburg.de)")
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

    // --- REISEN (Journeys) ---

    // 1. Seite anzeigen
    @GetMapping("/journeys")
    public String showJourneysPage(Model model) {
        model.addAttribute("journeyListe", journeyRepository.findAll());
        // WICHTIG: Wir brauchen die User für das Dropdown-Menü!
        model.addAttribute("usersListe", userRepository.findAll());
        return "journeys";
    }

    // 2. Reise speichern
    @PostMapping("/saveJourney")
    public String saveJourney(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam java.time.LocalDate startdate,
                              @RequestParam java.time.LocalDate enddate,
                              @RequestParam Long userId) {

        // Finde den User, der ausgewählt wurde
        User user = userRepository.findById(userId).orElseThrow();

        // Erstelle die Reise
        bienchen.summherum.entities.Journey newJourney = new bienchen.summherum.entities.Journey();
        newJourney.setTitle(title);
        newJourney.setDescription(description);
        newJourney.setStartdate(startdate);
        newJourney.setEnddate(enddate);
        newJourney.setUser(user); // Verknüpfung setzen!

        journeyRepository.save(newJourney);
        return "redirect:/journeys";
    }

    // --- EINTRÄGE (Entries) ---

     @GetMapping("/entries")
    public String showEntriesPage(Model model) {
        model.addAttribute("entryListe", entryRepository.findAll());
        model.addAttribute("journeyListe", journeyRepository.findAll());
        model.addAttribute("orteListe", travellocationRepository.findAll());
        return "entries";
    }

    // ==========================================
    // --- POST: Eintrag speichern (+ Wetter!) ---
    // ==========================================
    @PostMapping("/saveEntry")
    public String saveEntry(@RequestParam String title,
                            @RequestParam String text,
                            @RequestParam java.time.LocalDate date,
                            @RequestParam Long journeyId,
                            @RequestParam Long locationId) {

        // 1. Verknüpfungen laden
        bienchen.summherum.entities.Journey journey = journeyRepository.findById(journeyId).orElseThrow();
        // Wir brauchen den Ort zwingend, um die Koordinaten (Lat/Lon) zu wissen!
        bienchen.summherum.entities.Travellocation location = travellocationRepository.findById(locationId).orElseThrow();

        // 2. WETTER HOLEN (Neu!)
        String weatherSnapshot = null;

        try {
            // URL bauen (mit Lat/Lon vom Ort)
            String weatherUrl = String.format(Locale.US,
                    "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true",
                    location.getLatitude(), location.getLongitude());

            // API anrufen
            MeteoResponse response = webClient.get()
                    .uri(weatherUrl)
                    .retrieve()
                    .bodyToMono(MeteoResponse.class)
                    .block(); // Wir warten kurz auf die Antwort

              // Wenn Antwort da ist: Temperatur UND Text speichern
            if (response != null && response.getCurrentWeather() != null) {
                Double temp = response.getCurrentWeather().getTemperature();
                Integer code = response.getCurrentWeather().getWeathercode();

                // Wir kombinieren Temperatur und unseren übersetzten Text
                weatherSnapshot = temp + "°C, " + decodeWeatherCode(code);
            }
        } catch (Exception e) {
            System.out.println("Wetter konnte nicht geladen werden: " + e.getMessage());
            weatherSnapshot = "Keine Daten";
        }

        // 3. Eintrag bauen
        bienchen.summherum.entities.Entry newEntry = new bienchen.summherum.entities.Entry();
        newEntry.setTitle(title);
        newEntry.setText(text);
        newEntry.setTimestamp(date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

        newEntry.setJourney(journey);
        newEntry.setLocation(location);

        // Hier speichern wir das Wetter in die Datenbank!
        newEntry.setWeather(weatherSnapshot);

        // 4. Speichern
        entryRepository.save(newEntry);

        return "redirect:/entries";
    }

    // --- PACKLISTEN (Packinglists) ---

    @GetMapping("/packinglists")
    public String showPackinglistsPage(Model model) {
        model.addAttribute("packingListe", packinglistRepository.findAll());
        model.addAttribute("journeyListe", journeyRepository.findAll());
        return "packinglists";
    }

    @PostMapping("/savePackinglist")
    public String savePackinglist(@RequestParam String name,
                                  @RequestParam String season,
                                  @RequestParam String itemsText,
                                  @RequestParam Long journeyId) {

        // 1. Reise finden
        bienchen.summherum.entities.Journey journey = journeyRepository.findById(journeyId).orElseThrow();

        // 2. Packliste bauen
        bienchen.summherum.entities.Packinglist newList = new bienchen.summherum.entities.Packinglist();
        newList.setName(name);
        newList.setSeason(season);       // <-- Setzen der Saison
        newList.setItemsText(itemsText); // <-- Setzen des Textes
        newList.setJourney(journey);     // <-- Verknüpfung zur Reise

        // 3. Speichern
        packinglistRepository.save(newList);

        return "redirect:/packinglists";
    }

    // --- FEATURE 3: PACKLISTEN VORSCHLÄGE ---
    // Dieser Endpunkt gibt kein HTML zurück, sondern reinen Text (@ResponseBody)
// ==========================================
    // --- FEATURE 3: PACKLISTEN VORSCHLÄGE ---
    // ==========================================

    @GetMapping("/api/packing-suggestion")
    @ResponseBody
    public String getPackingSuggestion(@RequestParam String season) {

        // Kleinmachen und Leerzeichen weg
        String s = season.toLowerCase().trim();

        if (s.contains("frühling")) {
            return "- Leichte Jacke 🧥\n- Sonnenbrille 😎\n- Allergietabletten 🌸\n- Regenschirm (Aprilwetter!) ☔\n- Sneaker";
        }
        else if (s.contains("sommer")) {
            return "- Sonnencreme ☀️\n- Badehose/Bikini 🩳\n- Sonnenbrille 😎\n- Strandtuch\n- Flipflops";
        }
        else if (s.contains("herbst")) {
            return "- Regenjacke 🌧️\n- Gummistiefel\n- Regenschirm ☔\n- Kartenspiele\n- Mütze";
        }
        else if (s.contains("winter")) {
            return "- Dicke Jacke 🧥\n- Schal & Mütze 🧣\n- Handschuhe 🧤\n- Thermoskanne\n- Warme Socken";
        }
        else {
            // Standard (oder wenn man "Standard" eintippt)
            return "- Zahnbürste 🪥\n- Reisepass 🛂\n- Ladekabel 🔌\n- Unterwäsche\n- Medikamente";
        }
    }

    // 4. HILFSMETHODEN

    private Mono<String> findLocationPhoto(String queryName) {
        return webClient.get()
                .uri("https://api.pexels.com/v1/search?query=" + queryName + "&per_page=1")
                .header("Authorization", pexelsApiKey)
                .retrieve().bodyToMono(PexelsResponse.class)
                .map(res -> (res.getPhotos() != null && !res.getPhotos().isEmpty()) ? res.getPhotos().get(0).getSrc().getMedium() : null)
                .onErrorReturn("");
    }
    // Kleine Hilfsmethode zum Übersetzen der Wetter-API-Codes
    private String decodeWeatherCode(Integer code) {
        if (code == null) return "";
        if (code == 0) return "Klarer Himmel ☀️";
        if (code >= 1 && code <= 3) return "Teils bewölkt ☁️";
        if (code >= 45 && code <= 48) return "Nebel 🌫️";
        if (code >= 51 && code <= 55) return "Nieselregen 🌦️";
        if (code >= 61 && code <= 65) return "Regen 🌧️";
        if (code >= 71 && code <= 77) return "Schnee ❄️";
        if (code >= 80 && code <= 82) return "Regenschauer ☔";
        if (code >= 95) return "Gewitter ⚡";
        return "Trüb";
    }
}