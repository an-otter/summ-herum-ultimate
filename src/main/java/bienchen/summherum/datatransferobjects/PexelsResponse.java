package bienchen.summherum.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

// Schablonen für Jackson/Json Parser
@JsonIgnoreProperties(ignoreUnknown = true)
public class PexelsResponse {
    private List<PexelsPhoto> photos;

    public List<PexelsPhoto> getPhotos() { return photos; }
    public void setPhotos(List<PexelsPhoto> photos) { this.photos = photos; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PexelsPhoto {
        private PexelsSrc src;

        public PexelsSrc getSrc() { return src; }
        public void setSrc(PexelsSrc src) { this.src = src; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PexelsSrc {
        private String medium;

        public String getMedium() { return medium; }
        public void setMedium(String medium) { this.medium = medium; }
    }
}