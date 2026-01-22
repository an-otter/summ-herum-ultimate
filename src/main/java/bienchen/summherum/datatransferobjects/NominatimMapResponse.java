package bienchen.summherum.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// DTO für die OpenStreetMap Antwort
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimMapResponse {

    // Holt sich gezielt nur den vollen Adress-String ("display_name") aus dem JSON, um später statt Koordinaten die Adresse anzuzeigen
    @JsonProperty("display_name")
    private String displayName;

    // Getter und Setter
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

