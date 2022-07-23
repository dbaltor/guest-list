package list.guest.adapter.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor @Accessors(fluent = true)
public enum ErrorMessages {

    TABLE_NOT_FOUND("Table has not been found"),
    TABLE_CAPACITY_EXCEEDED("The table capacity has been exceeded"),
    TABLE_NOT_AVAILABLE("The table has already been reserved"),
    GUEST_BOOKED_ALREADY("The guest has already got a reservation"),
    GUEST_HAS_ALREADY_ARRIVED("The guest has already arrived"),
    GUEST_NOT_CHECKED_IN("The guest is not in the venue"),
    RESERVATION_NOT_FOUND("The guest has not got a valid reservation"),
    UNEXPECTED_ERROR("Unexpected error");

    @Getter()
    private final @NonNull String value;
    
}
