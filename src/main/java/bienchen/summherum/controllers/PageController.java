package bienchen.summherum.controllers;

import bienchen.summherum.datatransferobjects.MeteoResponse;
import bienchen.summherum.datatransferobjects.NominatimMapResponse;
import bienchen.summherum.datatransferobjects.PexelsResponse;
import bienchen.summherum.entities.*;
import bienchen.summherum.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

@Controller
public class PageController {

    @Autowired private TravellocationRepository travellocationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JourneyRepository journeyRepository;
    @Autowired private EntryRepository entryRepository;
    @Autowired private PackinglistRepository packinglistRepository;

    //WebClient für externe Aufrufe
    @Value("${api.pexels.key}") private String pexelsApiKey;
    private final WebClient webClient = WebClient.builder().build();

    // BLOCK A: STARTSEITE & NAVIGATION
    @GetMapping("/")
    public String showHomePage() {
        return "index"; // Zeigt index.html
    }
    // BLOCK B: USER
    @GetMapping("/users")
    public String showUsersPage(Model model) {
        model.addAttribute("usersListe", userRepository.findAll());
        return "users";
    }

    @PostMapping("/saveUser")
    public String saveUser(@RequestParam String username, @RequestParam String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        userRepository.save(newUser);
        return "redirect:/users";
    }

    // BLOCK C: LOGIN SYSTEM
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/performLogin")
    public String performLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        // Session Login; zuerst den User in Datenbank suchen
        User foundUser = null;
        for (User u : userRepository.findAll()) {
            if (u.getUsername().equals(username)) {
                foundUser = u;
                break;
            }
        }

        // danach Authentifizierung: Prüfen, ob User existiert und ob Passwort übereinstimmt
        if (foundUser != null && foundUser.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", foundUser); // User-Objekt in serverseitigen Session speichern und Cookie an Client senden, um eingeloggt zu bleiben
            return "redirect:/"; //zur Startseite zurück
        } else {
            model.addAttribute("error", true); // Login fehlgeschlagen
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/"; // ausgeloggt zur Startseite zurück
    }

    // BLOCK D: ORTE (Locations) + API (Nominatim & Pexels)
    @GetMapping("/showlocations")
    public String showLocationsPage(Model model) {
        model.addAttribute("orteListe", travellocationRepository.findAll());
        return "showlocations";
    }

    @GetMapping("/locations")
    public String showNewLocationPage() {
        return "locations"; // Formular zum Anlegen
    }

    // ★ BESONDERHEIT: Ruft Koordinaten ab UND sucht automatisch ein Foto
    @PostMapping("/saveLocation")
    public String saveLocationFromForm(@RequestParam Double latitude, @RequestParam Double longitude) {
        // 1. Nominatim API: Koordinaten werden zu Adressausgabe
        String nominatimUrl = String.format(Locale.US, "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f", latitude, longitude);

        webClient.get().uri(nominatimUrl)
                .header("User-Agent", "SummherumApp/1.0 (heike.ademmer@th-brandenburg.de)") // hier muss eine Email stehen, generische Mailadressen werden sonst nicht akzeptiert
                .retrieve().bodyToMono(NominatimMapResponse.class)
                .flatMap(res -> {
                    String name = res.getDisplayName();
                    // 2. Pexels API: aus der Nomatim Adresse  wird jetzt eine Foto URL generiert und später abgerufen
                    return findLocationPhoto(name).map(photo -> {
                        Travellocation loc = new Travellocation();
                        loc.setLatitude(latitude);
                        loc.setLongitude(longitude);
                        loc.setName(name);
                        loc.setImageUrl(photo);
                        return loc;
                    });
                })
                .flatMap(loc -> Mono.just(travellocationRepository.save(loc))) // abspeichern
                .block(); // warten bis alles fertig ist

        return "redirect:/showlocations";
    }

    // BLOCK E: REISEN (Journeys)
    @GetMapping("/journeys")
    public String showJourneysPage(Model model) {
        model.addAttribute("journeyListe", journeyRepository.findAll());
        model.addAttribute("usersListe", userRepository.findAll()); // Für Dropdowns
        return "journeys";
    }

    @PostMapping("/saveJourney")
    public String saveJourney(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam java.time.LocalDate startdate,
                              @RequestParam java.time.LocalDate enddate,
                              @RequestParam Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        Journey newJourney = new Journey();
        newJourney.setTitle(title);
        newJourney.setDescription(description);
        newJourney.setStartdate(startdate);
        newJourney.setEnddate(enddate);
        newJourney.setUser(user);

        journeyRepository.save(newJourney);
        return "redirect:/journeys";
    }

    // BLOCK F: EINTRÄGE (Entries) + Timeline + Wetter API
    @GetMapping("/entries")
    public String showEntriesPage(Model model) {
        model.addAttribute("entryListe", entryRepository.findAll());
        model.addAttribute("journeyListe", journeyRepository.findAll());
        model.addAttribute("orteListe", travellocationRepository.findAll());
        return "entries";
    }

    // ★ BESONDERHEIT: Sortierte Timeline bzw. Zeitstrahl nach Neueste zuerst
    @GetMapping("/timeline")
    public String showTimelinePage(Model model) {
        List<Entry> sortedEntries = entryRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        model.addAttribute("timelineList", sortedEntries);
        return "timeline";
    }

    // ★ BESONDERHEIT: Speichert Eintrag UND holt aktuelles Wetter
    @PostMapping("/saveEntry")
    public String saveEntry(@RequestParam String title,
                            @RequestParam String text,
                            @RequestParam java.time.LocalDate date,
                            @RequestParam Long journeyId,
                            @RequestParam Long locationId) {

        Journey journey = journeyRepository.findById(journeyId).orElseThrow();
        Travellocation location = travellocationRepository.findById(locationId).orElseThrow();

        //API Aufruf für's Wetter
        String weatherSnapshot = "Keine Daten";
        try {
            String weatherUrl = String.format(Locale.US,
                    "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true",
                    location.getLatitude(), location.getLongitude()); // baut aus Locationskoordinaten die URL

            MeteoResponse response = webClient.get()
                    .uri(weatherUrl).retrieve().bodyToMono(MeteoResponse.class).block(); // JSON von der API holen und das wird im DataTransferObject umgewandelt

            if (response != null && response.getCurrentWeather() != null) {
                Double temp = response.getCurrentWeather().getTemperature();
                Integer code = response.getCurrentWeather().getWeathercode();
                weatherSnapshot = temp + "°C, " + decodeWeatherCode(code); // Daten aus dem DatatransferObject ablesen
            }
        } catch (Exception e) {
            System.out.println("Wetter-Fehler: " + e.getMessage());
        }

        // Eintrag erstellen
        Entry newEntry = new Entry();
        newEntry.setTitle(title);
        newEntry.setText(text);
        newEntry.setTimestamp(date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        newEntry.setJourney(journey);
        newEntry.setLocation(location);
        newEntry.setWeather(weatherSnapshot); // Wetter Schnappschuss speichern

        entryRepository.save(newEntry);
        return "redirect:/entries";
    }

    // BLOCK G: PACKLISTEN
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

        Journey journey = journeyRepository.findById(journeyId).orElseThrow();

        Packinglist newList = new Packinglist();
        newList.setName(name);
        newList.setSeason(season);
        newList.setItemsText(itemsText);
        newList.setJourney(journey);

        packinglistRepository.save(newList);
        return "redirect:/packinglists";
    }

    // ★ BESONDERHEIT: "Magischer Zauberei-Button", der Textvorschläge direkt hinzufügt (API für Javascript)
    // Gibt reinen Text zurück, kein HTML!
    @GetMapping("/api/packing-suggestion")
    @ResponseBody
    public String getPackingSuggestion(@RequestParam String season) {
        String s = season.toLowerCase().trim();
        if (s.contains("frühling")) return "- Leichte Jacke 🧥\n- Sonnenbrille 😎\n- Sneaker";
        else if (s.contains("sommer")) return "- Sonnencreme ☀️\n- Badehose 🩳\n- Flipflops";
        else if (s.contains("herbst")) return "- Regenjacke 🌧️\n- Regenschirm ☔\n- Gummistiefel";
        else if (s.contains("winter")) return "- Dicke Jacke 🧥\n- Schal & Mütze 🧣\n- Thermoskanne";
        else return "- Zahnbürste 🪥\n- Reisepass 🛂\n- Ladekabel 🔌";
    }

    // nur einige HILFSMETHODEN
    // Sucht Foto bei Pexels
    private Mono<String> findLocationPhoto(String queryName) {
        return webClient.get()
                .uri("https://api.pexels.com/v1/search?query=" + queryName + "&per_page=1")
                .header("Authorization", pexelsApiKey) // ist in meiner application.yml
                .retrieve().bodyToMono(PexelsResponse.class)
                .map(res -> (res.getPhotos() != null && !res.getPhotos().isEmpty()) ? res.getPhotos().get(0).getSrc().getMedium() : null)
                .onErrorReturn("");
    }

    // Übersetzt Wetter-Code in Text für eine schönere Frontend Ausgabe
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