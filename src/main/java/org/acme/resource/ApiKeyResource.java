package org.acme.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/apikey/check")
public class ApiKeyResource {

    @GET
    public String status() {
        return "API Online 🔐";
    }
}
