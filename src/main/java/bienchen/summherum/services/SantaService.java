package bienchen.summherum.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SantaService {

    // Demo vom Vortrag

     @Cacheable("wünsche")
    public String getGiftsForRoom(String roomName) {

        try {
            // Simulation: Die Datenbank ist langsam!
             Thread.sleep(3000);

         // fängt Fehler, damit's nicht abstürzt
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


