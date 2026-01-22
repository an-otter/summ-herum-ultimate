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

    // NEU: Passwort
    private String password;

    @JsonIgnore // Verhindert Endlosschleifen, falls das Objekt mal als JSON ausgegeben wird
    @OneToMany(mappedBy = "user")
    private Set<Journey> journeys = new LinkedHashSet<>();

}