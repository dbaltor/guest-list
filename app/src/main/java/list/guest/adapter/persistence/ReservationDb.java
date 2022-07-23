package list.guest.adapter.persistence;

import org.springframework.data.annotation.Id;

import list.guest.dto.Guest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.RequiredArgsConstructor;

@ToString @Getter @NoArgsConstructor  @RequiredArgsConstructor(staticName = "of")
public class ReservationDb {
    
    private @Id Long id;
    private @NonNull String name;
    private @NonNull Integer tableNumber;
    private @NonNull Integer accompanyingGuests;

    public Guest guest() {
        return Guest.of(
            this.name,
            this.tableNumber,
            this.accompanyingGuests
        );
    }
}
