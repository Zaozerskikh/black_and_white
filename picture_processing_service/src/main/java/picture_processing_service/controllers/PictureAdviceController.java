package picture_processing_service.controllers;

import lombok.extern.java.Log;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import picture_processing_service.utils.PictureException;
import picture_processing_service.utils.PictureServerException;

/**
 * Exception handler, that handles PictureException, SizeException and PictureServerException.
 */
@Log
@RestControllerAdvice
public class PictureAdviceController {
    @ExceptionHandler(PictureException.class)
    public ResponseEntity<String> handlePictureException(PictureException exception) {
        log.info("REJECT: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(SizeException.class)
    public ResponseEntity<String> handleOversizeException(SizeException exception) {
        log.info("REJECT: File is too large");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is too large");
    }

    @ExceptionHandler(PictureServerException.class)
    public ResponseEntity<String> handleServerException(PictureServerException exception) {
        log.info("REJECT: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service unavailable. Try again later");
    }
}
