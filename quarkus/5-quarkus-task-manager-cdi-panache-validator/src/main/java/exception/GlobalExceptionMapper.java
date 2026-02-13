package exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof NotFoundException nfe) {
            return error(404, "Not Found", nfe.getMessage());
        }

        if (exception instanceof ConstraintViolationException cve) {
            var errors = cve.getConstraintViolations().stream()
                    .map(v -> Map.of(
                            "field", v.getPropertyPath().toString(),
                            "message", v.getMessage()
                    )).toList();
            return Response.status(400)
                    .entity(Map.of("status", 400, "error", "Validation Error",
                            "errors", errors, "timestamp", LocalDateTime.now().toString()))
                    .build();
        }

        if (exception instanceof IllegalArgumentException iae) {
            return error(400, "Bad Request", iae.getMessage());
        }

        return error(500, "Internal Server Error", "Erreur inattendue");
    }


    private Response error(int status, String error, String message) {
        return Response.status(status)
                .entity(Map.of("status", status, "error", error,
                        "message", message != null ? message : "",
                        "timestamp", LocalDateTime.now().toString()))
                .build();
    }
}
