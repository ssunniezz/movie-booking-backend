package io.muzoo.ssc.backend.repository;

import io.muzoo.ssc.backend.SeatReserved;
import io.muzoo.ssc.backend.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findByUsername(String username);
}
