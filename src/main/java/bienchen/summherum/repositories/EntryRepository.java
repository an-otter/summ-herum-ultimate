package bienchen.summherum.repositories;

import bienchen.summherum.entities.Entry;
import org.springframework.data.repository.CrudRepository;

public interface EntryRepository extends CrudRepository<Entry, Long> { }

