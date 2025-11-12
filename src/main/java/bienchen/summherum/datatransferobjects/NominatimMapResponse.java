package bienchen.summherum.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimMapResponse {

    // Holt sich das Feld "display_name" aus dem JSON
    @JsonProperty("display_name")
    private String displayName;

    // Standard-Getter und Setter
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

