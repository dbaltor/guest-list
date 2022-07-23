package list.guest.adapter.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TableRepository extends CrudRepository<TableDb, Long> {

    Optional<TableDb> findByTableNumber(Integer tableNumber);
}
