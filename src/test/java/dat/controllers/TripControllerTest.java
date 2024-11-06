package dat.controllers;

import dat.config.HibernateConfig;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TripControllerTest {

    private Javalin app;
    private TripController tripController;

    @BeforeAll
    public void setup() {
        // Initialize Javalin server and configure database connection
        app = Javalin.create();
        HibernateConfig.getEntityManagerFactory();
        tripController = new TripController();

        // Register routes
        app.get("/trips", tripController::getAllTrips);
        app.get("/trips/{id}", tripController::getTripById);
        app.post("/trips", tripController::createTrip);
        app.put("/trips/{id}", tripController::updateTrip);
        app.delete("/trips/{id}", tripController::deleteTrip);
        app.put("/trips/{tripId}/guides/{guideId}", tripController::addGuideToTrip);
        app.get("/trips/category/{category}", tripController::getTripByCategory);
        app.get("/trips/totalPricePerGuide", tripController::getTotalPricePerGuide);

        app.start(7000); // Start server on port 7000
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7000;
    }

    @AfterAll
    public void tearDown() {
        app.stop();
    }

    @Test
    public void testCreateTrip() {
        given()
                .body("{\"name\": \"New Trip\", \"startPosition\": \"City Center\", \"category\": \"CITY\", \"price\": 100}")
                .header("Content-Type", "application/json")
                .when()
                .post("/trips")
                .then()
                .statusCode(HttpStatus.CREATED.getCode())
                .body("name", equalTo("New Trip"));
    }

    @Test
    public void testGetAllTrips() {
        // Populate database for testing purposes
        given()
                .when()
                .post("/populateDatabase")
                .then()
                .statusCode(HttpStatus.OK.getCode());

        // Retrieve all trips and verify
        given()
                .when()
                .get("/trips")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("size()", greaterThan(0));
    }

    @Test
    public void testGetTripById() {
        // First, create a trip to retrieve by ID
        int tripId = given()
                .body("{\"name\": \"Test Trip\", \"startPosition\": \"Forest\", \"category\": \"FOREST\", \"price\": 200}")
                .header("Content-Type", "application/json")
                .when()
                .post("/trips")
                .then()
                .statusCode(HttpStatus.CREATED.getCode())
                .extract().path("id");

        // Now retrieve the created trip by ID and validate
        given()
                .pathParam("id", tripId)
                .when()
                .get("/trips/{id}")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("name", equalTo("Test Trip"));
    }

    @Test
    public void testUpdateTrip() {
        // First, create a trip to ensure there is a trip to update
        int tripId = given()
                .body("{\"name\": \"Initial Trip\", \"startPosition\": \"Old City\", \"category\": \"CITY\", \"price\": 100}")
                .header("Content-Type", "application/json")
                .when()
                .post("/trips")
                .then()
                .statusCode(HttpStatus.CREATED.getCode())
                .extract().path("id");

        // Update the created trip
        given()
                .body("{\"name\": \"Updated Trip\", \"startPosition\": \"New City\", \"category\": \"CITY\", \"price\": 150}")
                .header("Content-Type", "application/json")
                .pathParam("id", tripId)
                .when()
                .put("/trips/{id}")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("name", equalTo("Updated Trip"));
    }

    @Test
    public void testDeleteTrip() {
        // First, create a trip to ensure there is a trip to delete
        int tripId = given()
                .body("{\"name\": \"Trip to Delete\", \"startPosition\": \"Seaside\", \"category\": \"BEACH\", \"price\": 250}")
                .header("Content-Type", "application/json")
                .when()
                .post("/trips")
                .then()
                .statusCode(HttpStatus.CREATED.getCode())
                .extract().path("id");

        // Delete the created trip
        given()
                .pathParam("id", tripId)
                .when()
                .delete("/trips/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.getCode());
    }

    @Test
    public void testGetTripByCategory() {
        // Ensure there are trips with the "CITY" category in the database
        given()
                .body("{\"name\": \"City Tour\", \"startPosition\": \"Downtown\", \"category\": \"CITY\", \"price\": 50}")
                .header("Content-Type", "application/json")
                .when()
                .post("/trips")
                .then()
                .statusCode(HttpStatus.CREATED.getCode());

        given()
                .pathParam("category", "CITY")
                .when()
                .get("/trips/category/{category}")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("size()", greaterThan(0));
    }
}
