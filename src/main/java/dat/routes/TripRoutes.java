package dat.routes;

import dat.controllers.TripController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private static final TripController tripController = new TripController();

    /**
     * Definerer ruterne for Trip API, så de matcher opgavekravene.
     */
    public EndpointGroup getTripRoutes() {
        return () -> {
            // Task 3.3.1 - Hent alle trips
            get("/trips", tripController::getAllTrips, Role.ANYONE);

            // Task 3.3.1 - Hent en trip baseret på ID
            get("/trips/{id}", tripController::getTripById, Role.ANYONE);

            //Task 5.1 - Hent trips baseret på kategori
            get("/trips/category/{category}", tripController::getTripByCategory, Role.ANYONE);

            //Task 5.2 - Hent totalpris for trips pr. guide
            get("/trips/guides/totalprice", tripController::getTotalPricePerGuide,Role.USER);

            // Task 3.3.1 - Opret en ny trip
            post("/trips", tripController::createTrip, Role.USER);

            // Task 3.3.1 - Opdater en eksisterende trip
            put("/trips/{id}", tripController::updateTrip, Role.USER);

            // Task 3.3.1 - Slet en trip
            delete("/trips/{id}", tripController::deleteTrip, Role.USER);

            // Task 3.3.1 - Tilføj en eksisterende guide til en eksisterende trip
            put("/trips/{tripId}/guides/{guideId}", tripController::addGuideToTrip, Role.USER);

            // Task 3.3.1 - Populer databasen med trips og guider
           post("/trips/populate", tripController::populateDatabase, Role.ADMIN);
        };
    }
}
