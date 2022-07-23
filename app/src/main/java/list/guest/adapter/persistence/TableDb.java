package list.guest.adapter.persistence;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.RequiredArgsConstructor;

@ToString @Getter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class TableDb {

    private @Id Long id;
    private @NonNull Integer tableNumber;
    private @NonNull Integer tableCapacity;
}
