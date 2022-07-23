package list.guest.adapter.controller.dto;

import lombok.Data;
import lombok.NonNull;

@Data 
public class ReservationResponse {
    private @NonNull String name;

    public static ReservationResponse of(String name) {
        return new ReservationResponse(name);
    }
}
