package dat.routes;

import dat.controllers.TripController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private static final TripController TRIP_CONTROLLER = new TripController();

    public EndpointGroup getTripRoutes() {
        return () -> {
            get("/trips", TRIP_CONTROLLER::getAllTrips, Role.ANYONE);
            get("/trips/{id}", TRIP_CONTROLLER::getTripById, Role.USER);
            post("/trips", TRIP_CONTROLLER::createTrip, Role.USER);
            put("/trips/{id}", TRIP_CONTROLLER::updateTrip, Role.USER);
            delete("/trips/{id}", TRIP_CONTROLLER::deleteTrip, Role.USER);
            put("/trips/{tripId}/guides/{guideId}", TRIP_CONTROLLER::addGuideToTrip, Role.USER);
            post("/trips/populate", TRIP_CONTROLLER::populateDatabase, Role.ADMIN);
        };
    }
}
