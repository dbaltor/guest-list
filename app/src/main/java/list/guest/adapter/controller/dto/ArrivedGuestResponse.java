package list.guest.adapter.controller.dto;

import list.guest.dto.ArrivedGuest;
import lombok.Data;
import lombok.NonNull;

@Data
public class ArrivedGuestResponse {
    private @NonNull String name;
    private @NonNull String accompanying_guests;
    private @NonNull String time_arrived;
    
    public static ArrivedGuestResponse of(ArrivedGuest arrivedGuest) {
        return new ArrivedGuestResponse(
            arrivedGuest.getName(),
            arrivedGuest.getAccompanyingGuests().toString(),
            arrivedGuest.getTimeArrived().toString()
        );
    }
}
