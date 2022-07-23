package list.guest.adapter.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import list.guest.adapter.controller.dto.ReservationResponseError;
import list.guest.usecase.exceptions.GuestListException;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // 400 BAD REQUEST HANDLER
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        val errors = ex.getBindingResult()
            .getFieldErrors().stream()
            .collect(toMap(
                FieldError::getField,
                FieldError::getDefaultMessage));

        log.error("400 BAD REQUEST:\n" + errors);
        return handleExceptionInternal(ex, errors , headers, HttpStatus.BAD_REQUEST, request);
    }

    // 409 CONFLICT HANDLER
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(GuestListException.class)
    ReservationResponseError handleGuestListException(HttpServletRequest req, GuestListException ex) {
        
        String errorMsg;
        switch (ex.getErrorType()) {
            case TABLE_NOT_FOUND:
                errorMsg = ErrorMessages.TABLE_NOT_FOUND.value();
                break;
            case TABLE_CAPACITY_EXCEEDED:
                errorMsg = ErrorMessages.TABLE_CAPACITY_EXCEEDED.value();
                break;   
            case TABLE_NOT_AVAILABLE:
                errorMsg = ErrorMessages.TABLE_NOT_AVAILABLE.value();
                break;
            case GUEST_BOOKED_ALREADY:
                errorMsg = ErrorMessages.GUEST_BOOKED_ALREADY.value();
                break;
            case GUEST_HAS_ALREADY_ARRIVED:
            errorMsg = ErrorMessages.GUEST_HAS_ALREADY_ARRIVED.value();
            break;
            case GUEST_NOT_CHECKED_IN:
                errorMsg = ErrorMessages.GUEST_NOT_CHECKED_IN.value();
                break;                
            case RESERVATION_NOT_FOUND:
            errorMsg = ErrorMessages.RESERVATION_NOT_FOUND.value();
            break;
            default:
                errorMsg = ErrorMessages.UNEXPECTED_ERROR.value();
                break;
            }
            log.error("409 CONFLICT: " + errorMsg);
            return new ReservationResponseError(errorMsg);
    } 
}

