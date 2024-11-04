package dat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.config.HibernateConfig;
import dat.daos.TripDAO;
import dat.dtos.TripDTO;
import io.javalin.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TripController {

    private final TripDAO tripDAO;

    public TripController() {
        this.tripDAO = new TripDAO(HibernateConfig.getEntityManagerFactory());
    }

    public void getAllTrips(Context ctx) {
        List<TripDTO> trips = tripDAO.getAll();
        if (trips.isEmpty()) {
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.json(trips).status(HttpStatus.OK);
        }
    }

    public void getTripById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        TripDTO trip = tripDAO.getById(id);
        if (trip != null) {
            ctx.json(trip).status(HttpStatus.OK);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).json("Trip with ID " + id + " not found.");
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
            ctx.status(HttpStatus.NOT_FOUND).json("Trip with ID " + id + " not found.");
        }
    }

    public void deleteTrip(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        boolean deleted = tripDAO.delete(id);
        if (deleted) {
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).json("Trip with ID " + id + " not found.");
        }
    }

    public void addGuideToTrip(Context ctx) {
        int tripId = ctx.pathParamAsClass("tripId", Integer.class).get();
        int guideId = ctx.pathParamAsClass("guideId", Integer.class).get();
        try {
            tripDAO.addGuideToTrip(tripId, guideId);
            ctx.status(HttpStatus.OK).json("Guide added to trip successfully");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Error adding guide to trip: " + e.getMessage());
        }
    }

    public void populateDatabase(Context context) {
        tripDAO.populateDatabase();
        context.status(HttpStatus.OK).json("Database populated with trips");
    }

    // Task 5.1 - Filter trips by category
    public void getTripsByCategory(Context ctx) {
        String category = ctx.pathParam("category");
        List<TripDTO> trips = tripDAO.getAll().stream()
                .filter(trip -> trip.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        if (trips.isEmpty()) {
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.json(trips).status(HttpStatus.OK);
        }
    }

    // Task 5.2 - Total price of trips per guide
    public void getTotalPricePerGuide(Context ctx) {
        List<Map<String, Object>> totalPrices = tripDAO.getAll().stream()
                .collect(Collectors.groupingBy(
                        trip -> trip.getGuide().getId(),
                        Collectors.summingDouble(TripDTO::getPrice)
                ))
                .entrySet().stream()
                .map(entry -> Map.of("guideId", entry.getKey(), "totalPrice", entry.getValue()))
                .collect(Collectors.toList());
        ctx.json(totalPrices).status(HttpStatus.OK);
    }

    // Task 6.2 - Fetch packing items based on trip category
    public List<PackingItem> getPackingItemsForCategory(String category) throws IOException {
        String url = "https://packingapi.cphbusinessapps.dk/packinglist/" + category;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() == 200) {
            InputStream responseStream = connection.getInputStream();
            PackingListResponse response = objectMapper.readValue(responseStream, PackingListResponse.class);
            return response.getItems();
        } else {
            throw new IOException("Failed to fetch packing items from external API");
        }
    }

    // Task 6.4 - Calculate total weight of packing items for a trip
    public void getTotalWeightOfPackingItems(Context ctx) {
        String category = ctx.pathParam("category");
        try {
            double totalWeight = getPackingItemsForCategory(category).stream()
                    .mapToDouble(PackingItem::getWeightInGrams)
                    .sum();
            ctx.json(Map.of("totalWeight", totalWeight)).status(HttpStatus.OK);
        } catch (IOException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Error fetching packing items: " + e.getMessage());
        }
    }
}