package list.guest.adapter.controller.dto;

import lombok.Data;
import lombok.NonNull;

@Data 
public class SeatsEmptyResponse {
    private @NonNull String seats_empty;

    public static SeatsEmptyResponse of(int seatsEmpty) {
        return new SeatsEmptyResponse(String.valueOf(seatsEmpty));
    }
}
