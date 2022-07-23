package list.guest.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data @AllArgsConstructor(staticName = "of")
public class ArrivedGuest {
    private @NonNull String name;
    private @NonNull Integer accompanyingGuests;
    private @NonNull LocalDateTime timeArrived;    
}
