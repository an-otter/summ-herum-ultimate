package bienchen.summherum.controllers;

import bienchen.summherum.services.SantaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SantaController {
    // Wir holen uns unseren Service von oben
    @Autowired
    private SantaService santaService;

    // Die Seite erreichen wir unter: http://localhost:8081/santa
    @GetMapping("/santa")
    public String showGiftSack(@RequestParam(defaultValue = "A-1.08") String roomName,
                               Model model) {

        // 1. STOPPUHR STARTEN ⏱️
        // System.currentTimeMillis() = Aktuelle Zeit in Millisekunden
        long startTime = System.currentTimeMillis();

        // 2. DATEN ABFRAGEN (Der kritische Moment) 🐌 vs 🐆
        // Ohne Cache: Dauert 3 Sekunden.
        // Mit Cache: Dauert 0 Millisekunden.
        String gifts = santaService.getGiftsForRoom(roomName);

        // 3. STOPPUHR STOPPEN 🏁
        long endTime = System.currentTimeMillis();

        // 4. DAUER BERECHNEN
        long duration = endTime - startTime;

        // 5. Daten an das HTML schicken
        model.addAttribute("roomName", roomName);
        model.addAttribute("gifts", gifts);
        model.addAttribute("duration", duration);

        return "santa"; // Wir zeigen die Datei santa.html an
    }
}

