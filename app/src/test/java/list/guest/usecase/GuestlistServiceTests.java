/***************************\
   Unit Tests
\***************************/
package list.guest.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import list.guest.adapter.persistence.ArrivedGuestDb;
import list.guest.adapter.persistence.ArrivedGuestRepository;
import list.guest.adapter.persistence.ReservationDb;
import list.guest.adapter.persistence.ReservationRepository;
import list.guest.adapter.persistence.TableDb;
import list.guest.adapter.persistence.TableRepository;
import list.guest.usecase.exceptions.GuestListException;
import list.guest.usecase.exceptions.GuestListException.ErrorType;
import lombok.val;

public class GuestlistServiceTests {

    private static final int TABLE_NUMBER = 1;
    private static final int INVALID_TABLE_NUMBER = -1;
    private static final int NOT_FOUND_TABLE_NUMBER = 1;
    private static final int TABLE_CAPACITY = 10;
    private static final String GUEST_NAME = "Denis Baltor";
    private static final String UNKNOWN_GUEST = "UNKNOWN";
    private static final int SUCCESSFUL_ACCOMPANYING_NUMBER = 8;
    private static final int FAILED_ACCOMPANYING_NUMBER = 13;
    private static final LocalDateTime ARRIVAL_TIME = LocalDateTime.parse("2022-05-04T20:20:20");
    private static final int SEATS_EMPTY = 42;

    private GuestlistService service;
    private TableRepository tableRepository;
    private ReservationRepository reservationRepository;
    private ArrivedGuestRepository arrivedGuestRepository;
    private TableDb someTable;
    private List<ReservationDb> reservedGuestlist;
    private ReservationDb validReservation;
    private ReservationDb invalidReservation;
    private List<ArrivedGuestDb> arrivedGuestsDB;
    private ArrivedGuestDb arrivedGuestDB;

    @BeforeEach
    void setup() {
        this.tableRepository = mock(TableRepository.class);
        this.reservationRepository = mock(ReservationRepository.class);
        this.arrivedGuestRepository = mock(ArrivedGuestRepository.class);
        this.service = new GuestlistService(this.tableRepository, this.reservationRepository, this.arrivedGuestRepository);
        this.someTable = TableDb.of(TABLE_NUMBER, TABLE_CAPACITY);
        this.reservedGuestlist = List.of(
            ReservationDb.of("Guest 1", 10, 5),
            ReservationDb.of("Guest 2", 11, 6),
            ReservationDb.of("Guest 3", 12, 7),
            ReservationDb.of("Guest 4", 13, 8)
        );
        this.validReservation = ReservationDb.of(GUEST_NAME, TABLE_NUMBER, SUCCESSFUL_ACCOMPANYING_NUMBER);
        this.invalidReservation = ReservationDb.of(GUEST_NAME, INVALID_TABLE_NUMBER, SUCCESSFUL_ACCOMPANYING_NUMBER);
        this.arrivedGuestsDB = List.of(
            ArrivedGuestDb.of("Guest 1", SUCCESSFUL_ACCOMPANYING_NUMBER, ARRIVAL_TIME),
            ArrivedGuestDb.of("Guest 2", SUCCESSFUL_ACCOMPANYING_NUMBER, ARRIVAL_TIME),
            ArrivedGuestDb.of("Guest 3", SUCCESSFUL_ACCOMPANYING_NUMBER, ARRIVAL_TIME)
        );
        this.arrivedGuestDB = ArrivedGuestDb.of(GUEST_NAME, SUCCESSFUL_ACCOMPANYING_NUMBER, ARRIVAL_TIME);
    }

    @Test
    void given_TableHasSufficientSpace_when_GuestRequestToBookATable_then_GuestIsAddedToTheGuestlist () {
        // fail("Not yet implemented");
        // given
        when(tableRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.someTable));
        try {
            // when
            val guestName = this.service.bookTable(GUEST_NAME, TABLE_NUMBER, SUCCESSFUL_ACCOMPANYING_NUMBER);
            // then
            assertEquals(GUEST_NAME, guestName);
        } catch (GuestListException e) {
            fail(e.getErrorType().toString());
        }
    }
 
    @Test
    void given_TableDoesNotHaveSufficientSpace_when_GuestRequestToBookATable_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(tableRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.someTable));
        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.bookTable(GUEST_NAME, TABLE_NUMBER, FAILED_ACCOMPANYING_NUMBER);
            });
        // then
        assertEquals(ErrorType.TABLE_CAPACITY_EXCEEDED, exception.getErrorType());
    }

    @Test
    void given_TableIsNotFound_when_GuestRequestToBookATable_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(tableRepository.findByTableNumber(eq(NOT_FOUND_TABLE_NUMBER)))
            .thenReturn(Optional.empty());
        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.bookTable(GUEST_NAME, TABLE_NUMBER, SUCCESSFUL_ACCOMPANYING_NUMBER);
            });
        // then
        assertEquals(ErrorType.TABLE_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void given_TableHasAlreadyBeenReserved_when_GuestRequestToBookATable_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(tableRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.someTable));
        when(reservationRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.validReservation));

        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.bookTable(GUEST_NAME, TABLE_NUMBER, SUCCESSFUL_ACCOMPANYING_NUMBER);
            });
        // // then
        assertEquals(ErrorType.TABLE_NOT_AVAILABLE, exception.getErrorType());
    }

    @Test
    void given_TheGuestHasAlreadyAReservation_when_GuestRequestToBookATable_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(tableRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.someTable));
        when(reservationRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.validReservation));

        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.bookTable(GUEST_NAME, TABLE_NUMBER, SUCCESSFUL_ACCOMPANYING_NUMBER);
            });
        // // then
        assertEquals(ErrorType.GUEST_BOOKED_ALREADY, exception.getErrorType());
    }

    @Test
    void when_GuestlistIsRequested_then_ListOfAllGuestsIsReturned () {
        // fail("Not yet implemented");
        // given
        when(reservationRepository.findAll())
            .thenReturn(this.reservedGuestlist);
        // when
        val guestlist = this.service.getGuestlist();
        // then
        assertEquals(guestlist.size(), reservedGuestlist.size());
    }

    @Test
    void given_TheEntourageFitsTheTable_when_GuestArrives_then_TheyAreAllowedIn () {
        // fail("Not yet implemented");
        // given
        when(reservationRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.validReservation));
        when(tableRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.someTable));
        try {
            // when
            val guestName = this.service.checkGuestIn(GUEST_NAME, SUCCESSFUL_ACCOMPANYING_NUMBER);
            // then
            assertEquals(GUEST_NAME, guestName);
        }
        catch(GuestListException e) {
            fail(e.getErrorType().toString());
        }
    }

    @Test
    void given_TheEntourageDoesNotFitTheTable_when_GuestArrives_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(reservationRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.validReservation));
        when(tableRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.someTable));
        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.checkGuestIn(GUEST_NAME, FAILED_ACCOMPANYING_NUMBER);
            });
        // then
        assertEquals(ErrorType.TABLE_CAPACITY_EXCEEDED, exception.getErrorType());
    }

    @Test
    void given_TheReservationIsNotFound_when_GuestArrives_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(reservationRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.validReservation));
        when(tableRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.someTable));
        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.checkGuestIn(UNKNOWN_GUEST, SUCCESSFUL_ACCOMPANYING_NUMBER);
            });
        // then
        assertEquals(ErrorType.RESERVATION_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void given_TheTableReservedIsNotFound_when_GuestArrives_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(reservationRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.invalidReservation));
        when(tableRepository.findByTableNumber(eq(INVALID_TABLE_NUMBER)))
            .thenReturn(Optional.empty());
        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.checkGuestIn(GUEST_NAME, SUCCESSFUL_ACCOMPANYING_NUMBER);
            });
        // then
        assertEquals(ErrorType.TABLE_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void given_TheGuestIsAlreadyIn_when_GuestArrives_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(reservationRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.validReservation));
        when(tableRepository.findByTableNumber(eq(TABLE_NUMBER)))
            .thenReturn(Optional.of(this.someTable));
        when(arrivedGuestRepository.findByName(GUEST_NAME))
            .thenReturn(Optional.of(arrivedGuestDB));
        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.checkGuestIn(GUEST_NAME, SUCCESSFUL_ACCOMPANYING_NUMBER);
            });
        // then
        assertEquals(ErrorType.GUEST_HAS_ALREADY_ARRIVED, exception.getErrorType());
    }

    @Test
    void given_theGuestHasArrived_when_TheGuestLeaves_then_TheirEntourageLeaves () {
        // fail("Not yet implemented");
        // given
        when(reservationRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.validReservation));
        when(arrivedGuestRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.arrivedGuestDB));
        try {    
            // when
            this.service.checkGuestOut(GUEST_NAME);
            // then
            verify(arrivedGuestRepository, times(1))
                .delete(eq(this.arrivedGuestDB));
        } catch (GuestListException e) {
            fail(e.getErrorType().toString());
        }
    }

    @Test
    void given_theGuestHasNotArrived_when_TheGuestLeaves_then_AnErrorIsThrown () {
        // fail("Not yet implemented");
        // given
        when(reservationRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.of(this.validReservation));
        when(arrivedGuestRepository.findByName(eq(GUEST_NAME)))
            .thenReturn(Optional.empty());
        // when
        val exception = assertThrows(
            GuestListException.class, 
            () -> {
                this.service.checkGuestOut(GUEST_NAME);
            });
        // then
        assertEquals(ErrorType.GUEST_NOT_CHECKED_IN, exception.getErrorType());
    }

    @Test
    void when_TheArrivedGuestsAreRequested_then_TheListOfAllArrivedGuestsIsReturned () {
        // fail("Not yet implemented");
        // given
        when(arrivedGuestRepository.findAll())
            .thenReturn(this.arrivedGuestsDB);
        // when
        val guests = this.service.getArrivedGuests();
        // then
        assertEquals(arrivedGuestsDB.size(), guests.size());
    }

    @Test
    void when_TheNumberOfSeatsEmptyIsRequested_then_TheAvailableSeatsAcrossAllTabesIsReturned () {
        // fail("Not yet implemented");
        // given
        when(arrivedGuestRepository.countSeatsEmpty())
            .thenReturn(SEATS_EMPTY);
        // when
        val seatsEmpty = this.service.getSeatsEmpty();
        // then
        assertEquals(SEATS_EMPTY, seatsEmpty);
    }

}
