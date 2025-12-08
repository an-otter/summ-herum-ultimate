package bienchen.summherum.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Schablone für die äußere Hülle
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteoResponse {

    @JsonProperty("current_weather")
    private CurrentWeather currentWeather;

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    // Schablone für das Innere
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentWeather {
        private Double temperature;
        private Integer weathercode;

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