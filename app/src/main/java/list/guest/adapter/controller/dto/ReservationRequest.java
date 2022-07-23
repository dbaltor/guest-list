package list.guest.adapter.controller.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReservationRequest {
    
    @NotEmpty(message = "Table number is mandatory") 
    @Pattern(regexp = "^[0-9]{1,5}$", message = "Table number must be a number with up to 5 digits")
    private String table;

    @NotEmpty(message = "Number of accompanying guests is mandatory") 
    @Pattern(regexp = "^[0-9]{1,2}$", message = "Accompanying guests must be a number with up to 2 digits")
    private String accompanying_guests;    
}
