package io.muzoo.ssc.backend.repository;

import io.muzoo.ssc.backend.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Integer> {
}
