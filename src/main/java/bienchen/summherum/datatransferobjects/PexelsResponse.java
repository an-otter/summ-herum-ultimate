package bienchen.summherum.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

// DTO für Pexels Bilder-API
// JSON ist verschachtelt (Antwort -> Liste von Fotos -> Foto -> Quelle -> URL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PexelsResponse {

    private List<Photo> photos; // API liefert ein Array an Suchergebnissen

    public List<Photo> getPhotos() { return photos; }
    public void setPhotos(List<Photo> photos) { this.photos = photos; }

    // innere Klasse: Repräsentiert einzelnes Bild-Objekt in Array
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {
        private Src src; // "src"-Objekt enthält verschiedenen Bildgrößen

        public Src getSrc() { return src; }
        public void setSrc(Src src) { this.src = src; }
    }

    // weitere innere Klasse: tiefste Ebene, wo benötigte URL steht
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Src {
        private String medium; // nur URL für mittlere Bildgröße nehmen

        // Getter und Setter
        public String getMedium() { return medium; }
        public void setMedium(String medium) { this.medium = medium; }
    }
}