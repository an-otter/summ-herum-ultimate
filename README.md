## 🐝 Summherum - Projekt: Reisetagebuch

Willkommen bei einer Spring Boot Webanwendung zur Verwaltung und Erstellung von Reisen, Orten, Packlisten und Tagebucheinträgen. Die App ist mehr als ein simples Textfeld. Ich lade Sie ein, sich auf Ihre eigene User-Journey zu begeben.

## 🚀 Highlights
* **Reiseverwaltung:** Verknüpfung von Usern, Reisen und Orten
* **Live-Wetter:** Nutzt die *Open-Meteo API* zur Speicherung von Wetterdaten im Tagebuch
* **Automatische Bilder:** *Pexels API* und *Nominatim* ermitteln automatisch Adresse und Foto zu den Orten, statt nur Koordinaten anzuzeigen
* **Timeline:** Chronologische Darstellung der Einträge
* **automatische Packlisten** je nach Jahreszeit wird dynamisch eine vorhandene Packliste mit den Must-Haves vorgeschlagen
* **Caching:** simulierte Performance-Optimierung im Rahmen der Vortrags-Demo visualisiert

## ⚙️ Start

1.  **Projekt klonen**
2.  **Öffnen**
3.  **Starten:**
    Die Klasse `SummherumApplication.java` ausführen 
5.  **Browser:**
     * Lokal: [http://localhost:8081/](http://localhost:8081/)
    * Cloud: [https://bienoccio.ambitioushill-1c45cb57.polandcentral.azurecontainerapps.io](https://bienoccio.ambitioushill-1c45cb57.polandcentral.azurecontainerapps.io)
    * es kann sein, dass der Container eine längere Ladezeit hat, also bitte nicht vorzeitig abbrechen (wegen dem Cold-Start Phänomen)
    * die ideale Frontend User-Journey steht im E-Mail Anhang

## ⚠️ kurzer Hinweis zur Konfiguration bzgl. API Keys

Der **Pexels API Key** ist in der Datei `src/main/resources/application.yml` **bereits hinterlegt**, damit das Projekt für Korrekturzwecke direkt ausführbar ist.
> Ich weiß, dass in einer realen Produktionsumgebung dieser Key nie im Code stehen würde, sondern über Umgebungsvariablen injiziert wird, um die Sicherheit zu gewährleisten.
> Da es eine kostenlose Bild-API für einen kurzen Aufruf-Zeitraum ist, habe ich das so gelassen. Das Repository wird nach der Bewertung auch wieder auf "Private" gesetzt.

