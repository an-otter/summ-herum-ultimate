package bienchen.summherum.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // --- NEU: Passwort ---
    private String password;

    @JsonIgnore // ist dafür da, um keine Endlosschleife in der PostMapping Anzeige zu kriegen, weil 2-Wege Beziehung
    @OneToMany(mappedBy = "user")
    private Set<Journey> journeys = new LinkedHashSet<>();

}

// Musste Datenbank verändern, weil er mit der ID und dass initial null ist nie klarkam. 'NULL not allowed for column "ID"'
// Auto_Increment funktionierte nicht synchronisiert :( Deswegen folgende Änderung

//ALTER TABLE USERS ALTER COLUMN ID BIGINT AUTO_INCREMENT;
//ALTER TABLE JOURNEY ALTER COLUMN ID BIGINT AUTO_INCREMENT;
//ALTER TABLE ENTRY ALTER COLUMN ID BIGINT AUTO_INCREMENT;
//ALTER TABLE PACKINGLIST ALTER COLUMN ID BIGINT AUTO_INCREMENT;
//ALTER TABLE TRAVELLOCATION ALTER COLUMN ID BIGINT AUTO_INCREMENT;