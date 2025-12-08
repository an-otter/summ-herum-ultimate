package bienchen.summherum.repositories;

import bienchen.summherum.entities.Entry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface EntryRepository extends JpaRepository<Entry, Long> { }

