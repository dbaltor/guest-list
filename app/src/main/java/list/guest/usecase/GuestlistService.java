package list.guest.usecase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;
import static java.util.stream.Collectors.*;

import org.springframework.stereotype.Service;

import list.guest.adapter.persistence.ArrivedGuestDb;
import list.guest.adapter.persistence.ArrivedGuestRepository;
import list.guest.adapter.persistence.ReservationDb;
import list.guest.adapter.persistence.ReservationRepository;
import list.guest.adapter.persistence.TableRepository;
import list.guest.dto.ArrivedGuest;
import list.guest.dto.Guest;
import list.guest.usecase.exceptions.GuestListException;
import list.guest.usecase.exceptions.GuestListException.ErrorType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class GuestlistService {

    private @NonNull TableRepository tableRepository;
    private @NonNull ReservationRepository reservationRepository;
    private @NonNull ArrivedGuestRepository arrivedGuestRepository;

    /**
     * Books a table for a guest if it has capacity to seat the number of accompanying guests
     * @param guestName             the guest's name; they cannot have a reservation already.
     * @param tableNumber           the number of the table to book; a table with this number must exist and be available.
     * @param accompanyingNumber    the number of people expected to accompany the guest; they must fit in the table capacity.
     * @return  the guest's name.
     */
    public String bookTable(String guestName, int tableNumber, int accompanyingNumber) throws GuestListException {

        val tableDB = this.tableRepository.findByTableNumber(tableNumber)
            .orElseThrow(() -> GuestListException.of(ErrorType.TABLE_NOT_FOUND));
        // Guest cannot have a table reserved
        if(this.reservationRepository.findByName(guestName).isPresent()) {
            throw GuestListException.of(ErrorType.GUEST_BOOKED_ALREADY);            
        }
        // The table must be available
        if (this.reservationRepository.findByTableNumber(tableNumber).isPresent()) {
            throw GuestListException.of(ErrorType.TABLE_NOT_AVAILABLE);
        }
        // the table capacity has to be at least equals to the accompanying number + the guest
        if (tableDB.getTableCapacity() <= accompanyingNumber) { 
            throw GuestListException.of(ErrorType.TABLE_CAPACITY_EXCEEDED);
        }
        reservationRepository.save(ReservationDb.of(
            guestName, 
            tableNumber, 
            accompanyingNumber)
        );
        return guestName;
    }

    /**
     * Retrieves the list of all guests
     * @return  the list of guests
     * @see     Reservation class
     */
    public List<Guest> getGuestlist() {
            
        return StreamSupport.stream(this.reservationRepository.findAll().spliterator(), false)
            .map(ReservationDb::guest)
            .collect(toList());
    }

    /**
     * Checks a guest in when they arrives with their entourage
     * @param guestName          the guest's name with a valid reservation; the table number in the reservation must be valid; guest has not yet arrived.
     * @param accompanyingNumber the number of people accompanying the guest; the entourage must fit in the table capacity.
     * @return  the guest's name
     */
    public String checkGuestIn(String guestName, int accompanyingNumber) throws GuestListException {

        val reservationDB = reservationRepository.findByName(guestName)
            .orElseThrow(() -> GuestListException.of(ErrorType.RESERVATION_NOT_FOUND));
        // verify if the guest has already been checked in    
        if(arrivedGuestRepository.findByName(guestName).isPresent()) {
            throw GuestListException.of(ErrorType.GUEST_HAS_ALREADY_ARRIVED);
        }
        val tableDB = this.tableRepository.findByTableNumber(reservationDB.getTableNumber())
            // this error is not supposed to happen if our database is in a consistent state (reservation -> table)
            .orElseThrow(() -> GuestListException.of(ErrorType.TABLE_NOT_FOUND));
        // the table capacity has to be at least equals to the accompanying number + the guest
        if(tableDB.getTableCapacity() <= accompanyingNumber){
            throw GuestListException.of(ErrorType.TABLE_CAPACITY_EXCEEDED);
        }
        arrivedGuestRepository.save(
            ArrivedGuestDb.of(guestName, accompanyingNumber, LocalDateTime.now())
        );
        return guestName;
    }

   /**
     * Retrieves the list of arrived guests
     * @return  the list of guests in the venue at the moment
     * @see     ArrivedGuest class
     */
    public List<ArrivedGuest> getArrivedGuests() {

        return StreamSupport.stream(arrivedGuestRepository.findAll().spliterator(), false)
            .map(ArrivedGuestDb::arrival)
            .collect(toList());
    }

    /** 
     * Checks a guest out when they leaves with their entourage
     * @param guestName          the guest's name; the guest must have a valid reservation; the guest must be in the venue.
     */
    public void checkGuestOut(String guestName) throws GuestListException {

        // The guest must have a valid reservation
        reservationRepository.findByName(guestName)
            .orElseThrow(() -> GuestListException.of(ErrorType.RESERVATION_NOT_FOUND));
        val arrivedGuestDB = arrivedGuestRepository.findByName(guestName)
            .orElseThrow(() -> GuestListException.of(ErrorType.GUEST_NOT_CHECKED_IN));
        arrivedGuestRepository.delete(arrivedGuestDB);
    }

   /**
     * Counts the current number of seats empty across all tables in the venue
     * @return  the number of seats empty at the moment 
     */
    public int getSeatsEmpty() {

        return arrivedGuestRepository.countSeatsEmpty();
    }
}
