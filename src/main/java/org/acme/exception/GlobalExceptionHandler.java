package org.acme.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {


        if (e instanceof ConstraintViolationException cve) {

            Map<String, String> erros = new HashMap<>();

            for (ConstraintViolation<?> v : cve.getConstraintViolations()) {
                erros.put(v.getPropertyPath().toString(), v.getMessage());
            }

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "status", 400,
                            "erro", "Erro de validação",
                            "detalhes", erros
                    ))
                    .build();
        }


        if (e instanceof WebApplicationException wae) {

            int status = wae.getResponse().getStatus();

            return Response
                    .status(status)
                    .entity(Map.of(
                            "status", status,
                            "erro", wae.getMessage()
                    ))
                    .build();
        }


        e.printStackTrace();

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                        "status", 500,
                        "erro", "Erro interno no servidor",
                        "mensagem", e.getMessage()
                ))
                .build();
    }
}
