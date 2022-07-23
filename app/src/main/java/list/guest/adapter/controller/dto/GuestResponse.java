package list.guest.adapter.controller.dto;

import list.guest.dto.Guest;
import lombok.Data;
import lombok.NonNull;

@Data 
public class GuestResponse {
    private @NonNull String name;
    private @NonNull String table;
    private @NonNull String accompanying_guests;

    public static GuestResponse of(Guest guest) {
        return new GuestResponse(
            guest.getName(),
            guest.getTableNumber().toString(),
            guest.getAccompanyingGuests().toString()
        );
    }
}
