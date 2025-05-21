package com.example.riberpublicfichajeapi.excepciones;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExcepcionesHandler {

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(CredencialesInvalidasException ex) {
        ErrorResponse err = new ErrorResponse("CREDENCIALES_INVALIDAS", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(err);
    }

    /**
     * Clase interna para la respuesta del error
     */
    @Setter
    @Getter
    public static class ErrorResponse {
        private String code;
        private String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
