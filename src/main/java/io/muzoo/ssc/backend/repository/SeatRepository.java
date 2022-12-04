package io.muzoo.ssc.backend.repository;

import io.muzoo.ssc.backend.Seat;
import io.muzoo.ssc.backend.SeatReserved;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends CrudRepository<Seat, Integer> {

    Seat findSeatById(Long id);
}
