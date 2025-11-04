package bienchen.summherum.repositories;

import bienchen.summherum.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> { }
