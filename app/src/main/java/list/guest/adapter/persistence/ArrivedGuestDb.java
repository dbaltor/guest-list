package list.guest.adapter.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import list.guest.dto.ArrivedGuest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.RequiredArgsConstructor;

@ToString @Getter @NoArgsConstructor@RequiredArgsConstructor(staticName = "of")
public class ArrivedGuestDb {
    
    private @Id Long id;
    private @NonNull String name;
    private @NonNull Integer accompanyingGuests;
    private @NonNull LocalDateTime timeArrived;


    public ArrivedGuest arrival() {
        return ArrivedGuest.of(
            this.name,
            this.accompanyingGuests,
            this.timeArrived
        );
    }
}
