## 🐝 Summherum - Projekt: Reiseplanung & Tagebuch

Willkommen bei einer Spring Boot Webanwendung zur Verwaltung von Reisen, Orten, Packlisten und Tagebucheinträgen. Die App ist mehr als bloßes hineinschreiben, denn es integriert Wetterdaten, smarte Funktionen und Bilder für ein interaktives Reiseerlebnis.

## 🚀 Highlights
* **Reiseverwaltung:** Verknüpfung von Usern, Reisen und Orten
* **Live-Wetter:** Nutzt die *Open-Meteo API* zur Speicherung von Wetterdaten im Tagebuch
* **Automatische Bilder:** *Pexels API* und *Nominatim* finden automatisch Adresse und Foto zu den Orten, statt nur Koordinaten anzuzeigen
* **Caching:** Performance-Optimierung (simuliert) für Datenbankzugriffe im Rahmen der Vortrags-Demo
* **Timeline:** Chronologische Darstellung der Einträge
* **automatische Packlisten** je nach Jahreszeit wird dynamisch eine vorhandene Packliste mit den Must-Haves generiert

## ⚙️ Start

1.  **Projekt klonen**
2.  **Öffnen**
3.  **Starten und bewerten:**
    Die Klasse `SummherumApplication.java` ausführen sowie den Code reviewen
5.  **Browser:**
    Die Anwendung läuft unter: http://localhost:8081/ oder dem Azure Container aus der zugehörigen E-Mail

## ⚠️ kurzer Hinweis zur Konfiguration (API Keys)

Der **Pexels API Key** ist in der Datei `src/main/resources/application.yml` bereits hinterlegt, damit das Projekt für Korrekturzwecke direkt ausführbar ist 
> Ich weiß, dass in einer realen Produktionsumgebung dieser Key niemals im Code eingecheckt würde, sondern über Umgebungsvariablen injiziert wird, um die Sicherheit zu gewährleisten.
> Da es aber eine kostenlose Bild-API für einen kurzen Zeitraum ist, habe ich das so drin gelassen. Außerdem mache ich mein Github Repo danach wieder auf private.

## 👨‍💻 Verwendete Technologien
* Java 21
* Spring Boot (Web, JPA, Thymeleaf)
* H2 Database (In-Memory)
* Spring WebClient (Reactive)
* HTML5 / CSS3
