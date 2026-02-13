package com.foodexpress.customer.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Context
    UriInfo uriInfo;

    private static final String SERVICE_NAME = "customer-service";

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof ResourceNotFoundException) {
            return buildResponse(404, "Not Found", exception.getMessage());
        }
        if (exception instanceof DuplicateResourceException) {
            return buildResponse(409, "Conflict", exception.getMessage());
        }
        if (exception instanceof ConstraintViolationException cve) {
            return handleValidation(cve);
        }
        return buildResponse(500, "Internal Server Error", "Une erreur inattendue s'est produite");
    }

    private Response handleValidation(ConstraintViolationException cve) {
        List<ErrorResponse.FieldViolation> violations = cve.getConstraintViolations().stream()
                .map(v -> {
                    String field = "";
                    for (var node : v.getPropertyPath()) {
                        field = node.getName();
                    }
                    return new ErrorResponse.FieldViolation(field, v.getMessage());
                })
                .toList();

        ErrorResponse error = new ErrorResponse(
                400, "Validation Error",
                "La requête contient des erreurs de validation",
                uriInfo.getPath(), SERVICE_NAME);
        error.violations = violations;
        return Response.status(400).entity(error).build();
    }

    private Response buildResponse(int status, String error, String message) {
        ErrorResponse body = new ErrorResponse(status, error, message, uriInfo.getPath(), SERVICE_NAME);
        return Response.status(status).entity(body).build();
    }
}
