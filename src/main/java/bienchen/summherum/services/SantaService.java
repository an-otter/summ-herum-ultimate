package bienchen.summherum.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SantaService {
    // ANLEITUNG FÜR DEN VORTRAG:
    // 1. Am Anfang: Kommentiere @Cacheable aus (// davor setzen).
    // 2. Zeige, wie langsam es ist.
    // 3. Dann: Mach die // weg -> Cache ist aktiv!
    // ---------------------------------------------------------

    // @Cacheable: Der Zauberbefehl!
    // "wünsche": Das ist der Name des Fachs im Arbeitsspeicher.
       // Spring guckt erst in den Cache. Wenn leer -> Methode ausführen.

    @Cacheable("wünsche")
    public String getGiftsForRoom(String roomName) {

        try {
            // SIMULATION: Die Datenbank ist langsam!
            // Wir lassen den Computer künstlich 3 Sekunden (3000ms) warten.
            Thread.sleep(3000);

         // fängt Fehler, damit Konsole während der 3 sek schlafenszeit nicht abstürzt
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Das Ergebnis: Eine Wunschliste für den Raum
        if (roomName.equalsIgnoreCase("A-1.08")) {
            return "🎁 Glück\n🎁 gute Noten";
        }
            return "🎁 Neue Kreide";
        }
}


