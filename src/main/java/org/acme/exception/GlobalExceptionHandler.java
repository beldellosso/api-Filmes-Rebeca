package org.acme.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {

        if (e instanceof ConstraintViolationException cve) {
            String msg = cve.getConstraintViolations()
                    .stream()
                    .map(v -> v.getMessage())
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro interno: " + e.getMessage())
                .build();
    }
}
