package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    void handleIncorrectParameterException() {
        String parameter = "parameter";
        IncorrectParameterException e = new IncorrectParameterException(parameter);
        ErrorResponse result = errorHandler.handleIncorrectParameterException(e);
        assertEquals(result.getError(), String.format("Ошибка с полем \"%s\".", e.getParameter()));

    }

    @Test
    void handleInvalidEmailException() {
        String message = "Email is invalid";
        InvalidEmailException e = new InvalidEmailException(message);
        ErrorResponse result = errorHandler.handleInvalidEmailException(e);
        assertEquals(message, result.getError());
    }

    @Test
    void handleInvalidItemParametersException() {
        String message = "Entity not found";
        EntityNotFoundException e = new EntityNotFoundException(message);
        ErrorResponse result = errorHandler.handleEntityNotFoundException(e);
        assertEquals(message, result.getError());
    }

    @Test
    void handleEntityNotFoundException() {
        String message = "Entity not found";
        EntityNotFoundException e = new EntityNotFoundException(message);
        ErrorResponse result = errorHandler.handleEntityNotFoundException(e);
        assertEquals(message, result.getError());
    }

    @Test
    void handleUserAlreadyExistException() {
        String message = "User already exist";
        UserAlreadyExistException e = new UserAlreadyExistException(message);
        ErrorResponse result = errorHandler.handleUserAlreadyExistException(e);
        assertEquals(message, result.getError());
    }

    @Test
    void handleItemNotAvailableException() {
        String message = "Something thrown";
        Throwable e = new Throwable(message);
        ErrorResponse result = errorHandler.handleThrowable(e);
        assertEquals(message, result.getError());
    }
}