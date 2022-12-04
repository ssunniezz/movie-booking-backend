package io.muzoo.ssc.backend.repository;

import io.muzoo.ssc.backend.Auditorium;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriumRepository extends CrudRepository<Auditorium, Integer> {
}
