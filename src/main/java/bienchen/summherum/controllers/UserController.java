package bienchen.summherum.controllers;

import bienchen.summherum.entities.User;
import bienchen.summherum.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user1")
    public User getUser1() {
        return userRepository.findById(1L).orElseThrow();
    }

    // Hier können wir gleich dein @PostMapping hinzufügen!
}