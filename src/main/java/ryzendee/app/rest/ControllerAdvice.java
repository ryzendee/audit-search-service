package ryzendee.app.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ryzendee.app.dto.ErrorDetails;

import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleIllegalArgumentEx(IllegalArgumentException ex) {
        return new ErrorDetails(List.of(ex.getMessage()));
    }
}
