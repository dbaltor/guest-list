package list.guest.adapter.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import static java.util.stream.Collectors.*;

import javax.validation.Valid;

import list.guest.adapter.controller.dto.ArrivalNotificationRequest;
import list.guest.adapter.controller.dto.ArrivedGuestResponse;
import list.guest.adapter.controller.dto.GuestResponse;
import list.guest.adapter.controller.dto.ReservationRequest;
import list.guest.adapter.controller.dto.ReservationResponse;
import list.guest.adapter.controller.dto.SeatsEmptyResponse;
import list.guest.usecase.GuestlistService;
import list.guest.usecase.exceptions.GuestListException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.http.ResponseEntity;

import static org.springframework.http.MediaType.*;


@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GuestlistController {

    private @NonNull GuestlistService guestlistService;

    @PostMapping(value = "guest_list/{name}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationResponse> addGuest(@PathVariable("name") String guestName, @Valid @RequestBody ReservationRequest reservationRequest) throws GuestListException {

        val name = guestlistService.bookTable(
            guestName, 
            Integer.parseInt(reservationRequest.getTable()), 
            Integer.parseInt(reservationRequest.getAccompanying_guests()));
        return ResponseEntity.ok(ReservationResponse.of(name));
    }

    @GetMapping("guest_list")
    public ResponseEntity<List<GuestResponse>> getGuestList() {
        return ResponseEntity.ok(
            guestlistService.getGuestlist().stream()
                .map(GuestResponse::of)
                .collect(toList()));
    }

    @PutMapping(value = "guests/{name}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationResponse> notifyGuestArrival(@PathVariable("name") String guestName, @Valid @RequestBody ArrivalNotificationRequest arrivalNotificationRequest) throws GuestListException {
        val name = guestlistService.checkGuestIn(guestName, Integer.valueOf(arrivalNotificationRequest.getAccompanying_guests()));
        return ResponseEntity.ok(ReservationResponse.of(name));
    }

    @DeleteMapping("guests/{name}")
    public ResponseEntity<Void> notifyGuestDeparture(@PathVariable("name") String guestName) throws GuestListException {
        guestlistService.checkGuestOut(guestName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("guests")
    public ResponseEntity<List<ArrivedGuestResponse>> getArrivedGuests() {
        return ResponseEntity.ok(
            guestlistService.getArrivedGuests().stream()
                .map(ArrivedGuestResponse::of)
                .collect(toList()));
    }

    @GetMapping("seats_empty")
    public ResponseEntity<SeatsEmptyResponse> getSeatsEmpty() {
        return ResponseEntity.ok(
            SeatsEmptyResponse.of(guestlistService.getSeatsEmpty()));
    }
    
}
    
