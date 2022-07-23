/***************************\
   Acceptance Tests
\***************************/
package list.guest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.web.reactive.server.WebTestClient;

import list.guest.adapter.controller.ErrorMessages;
import list.guest.adapter.controller.dto.ArrivalNotificationRequest;
import list.guest.adapter.controller.dto.ReservationRequest;
import list.guest.adapter.persistence.ArrivedGuestRepository;
import list.guest.adapter.persistence.ReservationRepository;
import list.guest.adapter.persistence.TableDb;
import list.guest.adapter.persistence.TableRepository;
import lombok.val;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import java.util.stream.StreamSupport;

// Run these tests only when the "integration-tests" profile is active
	// @EnabledIf(
	// 	expression = "#{environment['spring.profiles.active'] == 'integration-tests'}", 
	// 	loadContext = true)
// The other way around. Activate "integration-tests" profile before running this test.
// @ActiveProfiles("integration-tests")
@SpringBootTest( 
    classes = MainApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class MainApplicationTests {
	
    private static final String TEST_GUEST = "Test Guest";
    private static final String ANOTHER_TEST_GUEST = "Another Test Guest";
    private static final int TABLE_NUMBER = 99999;
    private static final int TABLE_CAPACITY = 10;
    private static final int SUCCESSFUL_ACCOMPANYING_NUMBER = 8;
    private static final int FAILED_ACCOMPANYING_NUMBER = 13;

	private TableDb testTable;
	private ReservationRequest reservationRequest;
	private ReservationRequest invalidReservationRequest;
	private ReservationRequest invalidTableRequest;
	private ArrivalNotificationRequest arrivalNotificationRequest;
	private ArrivalNotificationRequest invalidArrivalNotificationRequest;

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private TableRepository tableRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private ArrivedGuestRepository arrivedGuestRepository;

	@BeforeAll
	void setup() {
		// save a table for tests
		this.testTable = tableRepository.save(TableDb.of(TABLE_NUMBER, TABLE_CAPACITY));

		this.reservationRequest = new ReservationRequest(String.valueOf(TABLE_NUMBER), String.valueOf(SUCCESSFUL_ACCOMPANYING_NUMBER));
		this.invalidReservationRequest = new ReservationRequest(String.valueOf(TABLE_NUMBER), String.valueOf(FAILED_ACCOMPANYING_NUMBER));
		this.invalidTableRequest = new ReservationRequest(String.valueOf(99998), String.valueOf(SUCCESSFUL_ACCOMPANYING_NUMBER));
		this.arrivalNotificationRequest = new ArrivalNotificationRequest(String.valueOf(SUCCESSFUL_ACCOMPANYING_NUMBER));
		this.invalidArrivalNotificationRequest = new ArrivalNotificationRequest(String.valueOf(FAILED_ACCOMPANYING_NUMBER));

	}
	@AfterAll
	void teardown() {
		// remove table used for tests
		tableRepository.delete(this.testTable);
	}

    @BeforeEach
    void beforeEachTest() {
		// Not required
	}

	@AfterEach
	void afterEachTest() {
		// clean up reservation made
		reservationRepository.findByName(TEST_GUEST)
			.ifPresent(reservation -> reservationRepository.delete(reservation));
		// clean up guest arrival
		arrivedGuestRepository.findByName(TEST_GUEST)
			.ifPresent(arrival -> arrivedGuestRepository.delete(arrival));
	}

	@Test
	void contextLoads() {
	}
/**************************\
 * Managing the guestlist *
\**************************/

    @Test
    void given_TableHasSufficientSpace_when_GuestRequestToBookATable_then_GuestIsAddedToTheGuestlist () {
		// when
		this.webTestClient
			.post()
			.uri("/guest_list/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(reservationRequest), ReservationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.name").isEqualTo(TEST_GUEST);
	}

    @Test
    void given_TableDoesNotHaveSufficientSpace_when_GuestRequestToBookATable_then_AnErrorIsThrown () {
		// when
		this.webTestClient
			.post()
			.uri("/guest_list/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(invalidReservationRequest), ReservationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.is4xxClientError()
			.expectBody()
			.jsonPath("$.reason").isEqualTo(ErrorMessages.TABLE_CAPACITY_EXCEEDED.value());
	}

    @Test
    void given_TableIsNotFound_when_GuestRequestToBookATable_then_AnErrorIsThrown () {
		// when
		this.webTestClient
			.post()
			.uri("/guest_list/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(invalidTableRequest), ReservationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.is4xxClientError()
			.expectBody()
			.jsonPath("$.reason").isEqualTo(ErrorMessages.TABLE_NOT_FOUND.value());
	}

    @Test
    void given_TableHasAlreadyBeenReserved_when_GuestRequestToBookATable_then_AnErrorIsThrown () {
		// given
		// the table has already been reserved  
		this.webTestClient
		.post()
		.uri("/guest_list/{name}", TEST_GUEST)
		.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
		.header(ACCEPT, APPLICATION_JSON_VALUE)
		.body(Mono.just(reservationRequest), ReservationRequest.class)
		.exchange()
		.expectStatus()
		.isOk();
		// when
		this.webTestClient
			.post()
			.uri("/guest_list/{name}", ANOTHER_TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(reservationRequest), ReservationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.is4xxClientError()
			.expectBody()
			.jsonPath("$.reason").isEqualTo(ErrorMessages.TABLE_NOT_AVAILABLE.value());
	}

    @Test
    void given_TheGuestHasAlreadyAReservation_when_GuestRequestToBookATable_then_AnErrorIsThrown () {
		// given
		// guest has got a reservation
		this.webTestClient
		.post()
		.uri("/guest_list/{name}", TEST_GUEST)
		.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
		.header(ACCEPT, APPLICATION_JSON_VALUE)
		.body(Mono.just(reservationRequest), ReservationRequest.class)
		.exchange()
		.expectStatus()
		.isOk();
		// when
		this.webTestClient
			.post()
			.uri("/guest_list/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(reservationRequest), ReservationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.is4xxClientError()
			.expectBody()
			.jsonPath("$.reason").isEqualTo(ErrorMessages.GUEST_BOOKED_ALREADY.value());
	}

    @Test
    void when_GuestlistIsRequested_then_ListOfAllGuestsIsReturned () {
		// given
		val numberOfGuests = (int)reservationRepository.count();
		// when
		this.webTestClient
			.get()
			.uri("/guest_list")
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.length()").isEqualTo(numberOfGuests);		
	}

/******************************\
 * Controlling the attendance *
\******************************/

    @Test
    void given_TheEntourageFitsTheTable_when_GuestArrives_then_TheyAreAllowedIn () {
		// given
		// guest has got a reservation
		this.webTestClient
		.post()
		.uri("/guest_list/{name}", TEST_GUEST)
		.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
		.header(ACCEPT, APPLICATION_JSON_VALUE)
		.body(Mono.just(reservationRequest), ReservationRequest.class)
		.exchange()
		.expectStatus()
		.isOk();
		// when
		this.webTestClient
			.put()
			.uri("/guests/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(arrivalNotificationRequest), ArrivalNotificationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.name").isEqualTo(TEST_GUEST);
	}

    @Test
    void given_TheEntourageDoesNotFitTheTable_when_GuestArrives_then_AnErrorIsThrown () {
		// given
		// guest has got a reservation
		this.webTestClient
			.post()
			.uri("/guest_list/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(reservationRequest), ReservationRequest.class)
			.exchange()
			.expectStatus()
			.isOk();
		// when
		this.webTestClient
			.put()
			.uri("/guests/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(invalidArrivalNotificationRequest), ArrivalNotificationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.is4xxClientError()
			.expectBody()
			.jsonPath("$.reason").isEqualTo(ErrorMessages.TABLE_CAPACITY_EXCEEDED.value());
	}

    @Test
    void given_TheReservationIsNotFound_when_GuestArrives_then_AnErrorIsThrown () {
		// when
		this.webTestClient
			.put()
			.uri("/guests/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(arrivalNotificationRequest), ArrivalNotificationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.is4xxClientError()
			.expectBody()
			.jsonPath("$.reason").isEqualTo(ErrorMessages.RESERVATION_NOT_FOUND.value());
	}

    @Test
    void given_TheGuestIsAlreadyIn_when_GuestArrives_then_AnErrorIsThrown () {
		// given
		// guest has got a reservation
		this.webTestClient
			.post()
			.uri("/guest_list/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(reservationRequest), ReservationRequest.class)
			.exchange()
			.expectStatus()
			.isOk();
		// and has already arrived
		this.webTestClient
			.put()
			.uri("/guests/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(arrivalNotificationRequest), ArrivalNotificationRequest.class)
			.exchange()
			.expectStatus()
			.isOk();
		// when
		this.webTestClient
			.put()
			.uri("/guests/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(arrivalNotificationRequest), ArrivalNotificationRequest.class)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.is4xxClientError()
			.expectBody()
			.jsonPath("$.reason").isEqualTo(ErrorMessages.GUEST_HAS_ALREADY_ARRIVED.value());
	}

    @Test
    void given_theGuestHasArrived_when_TheGuestLeaves_then_TheirEntourageLeaves () {
		// given
		// guest has got a reservation
		this.webTestClient
		.post()
		.uri("/guest_list/{name}", TEST_GUEST)
		.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
		.header(ACCEPT, APPLICATION_JSON_VALUE)
		.body(Mono.just(reservationRequest), ReservationRequest.class)
		.exchange()
		.expectStatus()
		.isOk();
		// and has arrived
		this.webTestClient
			.put()
			.uri("/guests/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.body(Mono.just(arrivalNotificationRequest), ArrivalNotificationRequest.class)
			.exchange()
			.expectStatus()
			.isOk();
		// when
		this.webTestClient
			.delete()
			.uri("/guests/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.exchange()
		// then
			.expectStatus()
			.isNoContent();
	}

    @Test
    void given_theGuestHasNotArrived_when_TheGuestLeaves_then_AnErrorIsThrown () {
		// given
		// guest has got a reservation
		this.webTestClient
		.post()
		.uri("/guest_list/{name}", TEST_GUEST)
		.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
		.header(ACCEPT, APPLICATION_JSON_VALUE)
		.body(Mono.just(reservationRequest), ReservationRequest.class)
		.exchange()
		.expectStatus()
		.isOk();
		// when
		this.webTestClient
			.delete()
			.uri("/guests/{name}", TEST_GUEST)
			.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.header(ACCEPT, APPLICATION_JSON_VALUE)
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.is4xxClientError()
			.expectBody()
			.jsonPath("$.reason").isEqualTo(ErrorMessages.GUEST_NOT_CHECKED_IN.value());
	}

/*******************************\
 * Reporting on the attendance *
\*******************************/
	
	@Test
    void when_TheArrivedGuestsAreRequested_then_TheListOfAllArrivedGuestsIsReturned () {
		// given
		val numberOfGuestsIn = (int)arrivedGuestRepository.count();
		// when
		this.webTestClient
			.get()
			.uri("/guests")
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.length()").isEqualTo(numberOfGuestsIn);		
	}

    @Test
    void when_TheNumberOfSeatsEmptyIsRequested_then_TheAvailableSeatsAcrossAllTabesIsReturned () {
			// given
			val totalSeats = StreamSupport.stream(tableRepository.findAll().spliterator(), false)
				.map(table -> table.getTableCapacity())
				.mapToInt(Integer::intValue)
				.sum();
			val arrivedGuests = StreamSupport.stream(arrivedGuestRepository.findAll().spliterator(), false)
				.map(arrivedGuest -> arrivedGuest.getAccompanyingGuests() + 1) // add the hostre
				.mapToInt(Integer::intValue)
				.sum();	
			// when
			this.webTestClient
				.get()
				.uri("/seats_empty")
				.exchange()
			// then
				.expectHeader()
				.contentType(APPLICATION_JSON)
				.expectStatus()
				.isOk()
				.expectBody()
				.jsonPath("$.seats_empty").isEqualTo(totalSeats - arrivedGuests);		
	}

}
