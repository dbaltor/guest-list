package list.guest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data @AllArgsConstructor(staticName = "of")
public class Guest {
    private @NonNull String name;
    private @NonNull Integer tableNumber;
    private @NonNull Integer accompanyingGuests;
}
