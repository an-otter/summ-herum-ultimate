package bienchen.summherum.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// DTO für  Wetter-API
@JsonIgnoreProperties(ignoreUnknown = true) // ignoriert überflüssige Daten
public class MeteoResponse {

    @JsonProperty("current_weather") // verbindet JSON-Feld mit Java-Variable
    private CurrentWeather currentWeather;

    // Getter und Setter
    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    // statische Klasse bildet verschachtelte JSON-Struktur nach
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentWeather {
        private Double temperature;
        private Integer weathercode; // Zahlencode wird später im Controller übersetzt

        // Getter und Setter
        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Integer getWeathercode() { return weathercode; }
        public void setWeathercode(Integer weathercode) { this.weathercode = weathercode; }
    }
}