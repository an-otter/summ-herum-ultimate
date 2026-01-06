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

        // Aktuelle Zeit messen
        long startTime = System.currentTimeMillis();

        // Service aufrufen
        String gifts = santaService.getGiftsForRoom(roomName);

        long endTime = System.currentTimeMillis();

        //  DAUER berechnen
        long duration = endTime - startTime;

        // Daten an das HTML schicken
        model.addAttribute("roomName", roomName);
        model.addAttribute("gifts", gifts);
        model.addAttribute("duration", duration);

        return "santa"; // zeigt Datei santa.html an
    }
}

