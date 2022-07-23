package list.guest.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ArrivedGuestRepository extends CrudRepository<ArrivedGuestDb, Long> {
    
    Optional<ArrivedGuestDb> findByName(String name);

    @Query(
        "SELECT SUM(t.table_capacity - COALESCE(a.accompanying_guests + 1, 0))\n" +
        "FROM table_db t\n" +
        "LEFT JOIN reservation_db r ON t.table_number = r.table_number\n" +
        "LEFT JOIN arrived_guest_db a ON r.name = a.name"
    )
    int countSeatsEmpty();    

}
