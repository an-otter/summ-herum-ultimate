package bienchen.summherum.controllers;

import bienchen.summherum.entities.Journey;
import bienchen.summherum.entities.User;
import bienchen.summherum.repositories.JourneyRepository;
import bienchen.summherum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@RestController
public class JourneyController {

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/journey1") //http://localhost:8081/journey1
    public Journey getJourney1(){
        return journeyRepository.findById(1L).orElseThrow();
    }

    /* Erstellt eine neue Reise.
            * Braucht Titel, Beschreibung, Daten und die ID des Users,
            * dem die Reise gehört.
            */
    @PostMapping("/createJourney")
    public Journey createJourney(@RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam LocalDate startdate,
                                 @RequestParam LocalDate enddate,
                                 @RequestParam Long userId) {

        // 2. Finde den User, dem diese Reise gehören soll
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 3. Erstelle die neue Reise
        Journey newJourney = new Journey();
        newJourney.setTitle(title);
        newJourney.setDescription(description);
        newJourney.setStartdate(startdate);
        newJourney.setEnddate(enddate);
        newJourney.setUser(user); // Verknüpft den User

        // 4. Speichern und zurückgeben
        return journeyRepository.save(newJourney);
    }
}
