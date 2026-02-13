package com.example.taskmanager.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    // Record pour la réponse d'erreur structurée
    public record ErrorResponse(
            int status,
            String message,
            List<String> errors,
            String timestamp
    ) {}

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof ConstraintViolationException cve) {
            List<String> errors = cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                    .toList();
            return Response.status(400)
                    .entity(new ErrorResponse(400, "Erreurs de validation", errors, LocalDateTime.now().toString()))
                    .build();
        }

        if (exception instanceof NotFoundException nfe) {
            return Response.status(404)
                    .entity(new ErrorResponse(404, nfe.getMessage(), List.of(), LocalDateTime.now().toString()))
                    .build();
        }

        // Erreur générique (ne pas exposer la stack trace !)
        return Response.status(500)
                .entity(new ErrorResponse(500, "Erreur interne du serveur", List.of(), LocalDateTime.now().toString()))
                .build();
    }
}
