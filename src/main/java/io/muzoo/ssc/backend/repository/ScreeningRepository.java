package io.muzoo.ssc.backend.repository;

import io.muzoo.ssc.backend.Screening;
import io.muzoo.ssc.backend.SeatReserved;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreeningRepository extends CrudRepository<Screening, Integer> {

    Screening findScreeningById(Long id);
}
