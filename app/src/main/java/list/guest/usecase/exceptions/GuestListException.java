package list.guest.usecase.exceptions;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class GuestListException extends Exception{

    public static enum ErrorType {
        TABLE_NOT_FOUND,
        TABLE_CAPACITY_EXCEEDED,
        RESERVATION_NOT_FOUND,
        GUEST_NOT_CHECKED_IN,
        TABLE_NOT_AVAILABLE,
        GUEST_BOOKED_ALREADY,
        GUEST_HAS_ALREADY_ARRIVED
    }

    private @NonNull @Getter ErrorType errorType;
}