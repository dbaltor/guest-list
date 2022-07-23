package list.guest.adapter.controller.dto;

import lombok.Data;
import lombok.NonNull;

@Data 
public class ReservationResponseError {
    private @NonNull String reason;    
}
