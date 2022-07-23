package list.guest.adapter.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<ReservationDb, Long> {

    Optional<ReservationDb> findByName(String name);

    Optional<ReservationDb> findByTableNumber(Integer tableNumber);

}
