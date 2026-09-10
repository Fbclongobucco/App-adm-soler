package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.exception.AccommodationNotFoundException;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.ClientNotFoundException;
import com.buccodev.adm_soler.application.exception.EmployeeNotFoundException;
import com.buccodev.adm_soler.application.exception.EquipmentNotFoundException;
import com.buccodev.adm_soler.application.exception.InvalidCredentialsException;
import com.buccodev.adm_soler.application.exception.ProjectNotFoundException;
import com.buccodev.adm_soler.application.exception.RestaurantNotFoundException;
import com.buccodev.adm_soler.application.exception.UserAlreadyExistsException;
import com.buccodev.adm_soler.application.exception.UserNotFoundException;
import com.buccodev.adm_soler.core.exception.DomainException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AddressNotFoundException.class, ClientNotFoundException.class,
            EmployeeNotFoundException.class, EquipmentNotFoundException.class,
            ProjectNotFoundException.class, RestaurantNotFoundException.class,
            AccommodationNotFoundException.class, UserNotFoundException.class})
    public ProblemDetail handleNotFound(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(UserAlreadyExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource");
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomain(DomainException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid request body");
        problem.setProperty("fieldErrors", fieldErrors);
        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "This resource cannot be deleted because it is still referenced by other records");
    }
}
