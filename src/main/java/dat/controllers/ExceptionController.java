package dat.controllers;

import dat.exceptions.ApiException;
import dat.exceptions.Message;
import dat.routes.Routes;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controllerklasse til håndtering af exceptions.
 */
public class ExceptionController {
    private final Logger LOGGER = LoggerFactory.getLogger(Routes.class);//Logger referer til routes.class


    /**
     * Den her metode håndterer API-fejl og returnerer en standard JSON-fejlmeddelelse.
     */
    public void apiExceptionHandler(ApiException e, Context ctx) {
        LOGGER.error("API Error: " + e.getMessage());
        ctx.status(e.getStatusCode());
        ctx.json(new Message(e.getStatusCode(), e.getMessage()));
    }

    public void generalExceptionHandler(Exception e, Context ctx) {
        LOGGER.error("Unexpected Error: " + e.getMessage());
        ctx.status(500);
        ctx.json(new Message(500, "Server Error: " + e.getMessage()));
    }
}

/**
 *Error handling er inkluderet
 */
