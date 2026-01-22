package bienchen.summherum.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class HelloController {

// das hier war meine Startseite, bevor alle weiteren Funktionen hinzukamen, um zu sehen ob die Konfigurationen korrekt funktionieren und es bei localhost geladen werden konnte
// insbesondere als ich nachträglich in den Kurs kam, konnte ich so prüfen ob ich das Spring Boot Projekt erstmal richtig aufgesetzt hatte
    @GetMapping("start")
    public String sagHallo() {
        return """
                <html>
                    <body style="text-align: center; font-family: sans-serif;">
                        <h1>Summ summ, Bienchen summt herum!</h1>
                        <img src="https://images.thalia.media/00/-/4236f8eac73d4101ad25056af22bdb4d/legami-pluesch-super-soft-biene-legami.jpeg"
                             alt="Mein Lieblingsbienchen"
                             width="300px">
                    </body>
                </html>
               """;
    }
}