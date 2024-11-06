package dat.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.daos.TripDAO;
import dat.dtos.TripDTO;
import dat.exceptions.ApiException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlleren håndtere alle HTTP requests for trips. Kommunikerer med klienten ved at sende Json-respons og statuskode
 */
public class TripController {
    private final TripDAO tripDAO;


    public TripController() {
        this.tripDAO = new TripDAO(HibernateConfig.getEntityManagerFactory());
    }

    public void getAllTrips(Context ctx) {
        try {
            List<TripDTO> trips = tripDAO.getAll();
            if (trips.isEmpty()) {
                ctx.status(HttpStatus.NO_CONTENT);
            } else {
                ctx.json(trips).status(HttpStatus.OK);
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of(
                    "error", "Error retrieving all trips",
                    "details", e.getMessage()
            ));
            e.printStackTrace();
        }
    }


    public void getTripById(Context ctx) throws ApiException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        TripDTO trip = tripDAO.getById(id);
        if (trip != null) {
            try {
                JsonNode packingItems = getPackingItemsForCategory(trip.getCategory().toString().toLowerCase());
                ctx.json(Map.of("trip", trip, "packingItems", packingItems)).status(HttpStatus.OK);
            } catch (IOException e) {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Error fetching packing items: " + e.getMessage());
            }
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND.getCode(), "Trip with ID " + id + " was not found.");
        }
    }

    public void createTrip(Context ctx) {
        TripDTO newTrip = ctx.bodyAsClass(TripDTO.class);
        TripDTO createdTrip = tripDAO.create(newTrip);
        ctx.json(createdTrip).status(HttpStatus.CREATED);
    }

    public void updateTrip(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        TripDTO updatedTrip = ctx.bodyAsClass(TripDTO.class);
        TripDTO existingTrip = tripDAO.update(id, updatedTrip);
        if (existingTrip != null) {
            ctx.json(existingTrip).status(HttpStatus.OK);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).json("Trip with ID " + id + " was not found.");
        }
    }

    public void deleteTrip(Context ctx) throws ApiException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        boolean deleted = tripDAO.delete(id);
        if (deleted) {
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND.getCode(), "Trip with ID " + id + " was not found.");
        }
    }

    public void addGuideToTrip(Context ctx) {
        int tripId = ctx.pathParamAsClass("tripId", Integer.class).get();
        int guideId = ctx.pathParamAsClass("guideId", Integer.class).get();
        try {
            tripDAO.addGuideToTrip(tripId, guideId);
            ctx.status(HttpStatus.OK).json("Guide added to the trip.");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Error adding guide to the trip: " + e.getMessage());
        }
    }

    public void getTripByCategory(Context ctx) {
        String category = ctx.pathParam("category").toLowerCase();
        List<TripDTO> trips = tripDAO.getAll().stream()
                .filter(trip -> trip.getCategory().toString().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        if (trips.isEmpty()) {
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.json(trips).status(HttpStatus.OK);
        }
    }

    public void getTotalPricePerGuide(Context ctx) {
        Map<Long, Double> totalPrices = tripDAO.getAll().stream()
                .filter(trip -> trip.getGuide() != null)
                .collect(Collectors.groupingBy(
                        trip -> trip.getGuide().getId(),
                        Collectors.summingDouble(TripDTO::getPrice)
                ));
        ctx.json(totalPrices).status(HttpStatus.OK);
    }

    public void populateDatabase(Context context) {
        tripDAO.populateDatabase();
        context.status(HttpStatus.OK).json("Database populated with sample data.");
    }

    /**
     * Fetches all trips for a specific guide based on guide ID.
     */
    public void getTripsByGuide(Context ctx) {
        int guideId = ctx.pathParamAsClass("guideId", Integer.class).get();
        Set<TripDTO> trips = tripDAO.getTripsByGuide(guideId);
        if (trips.isEmpty()) {
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.json(trips).status(HttpStatus.OK);
        }
    }

    public JsonNode getPackingItemsForCategory(String category) throws IOException {
        String url = "https://packingapi.cphbusinessapps.dk/packinglist/" + category;
        ObjectMapper objectMapper = new ObjectMapper();

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() == 200) {
            InputStream responseStream = connection.getInputStream();
            return objectMapper.readTree(responseStream).get("items");
        } else {
            throw new IOException("Error fetching packing items from the API.");
        }
    }
}
