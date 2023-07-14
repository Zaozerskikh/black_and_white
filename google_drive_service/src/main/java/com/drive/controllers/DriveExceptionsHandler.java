package com.drive.controllers;

import com.drive.exceptions.DriveAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class DriveExceptionsHandler {
    @ExceptionHandler(DriveAuthException.class)
    public ResponseEntity<String> handlePictureException(DriveAuthException exception) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:8080/oauth2/authorization/google"))
                .build();
    }
}
