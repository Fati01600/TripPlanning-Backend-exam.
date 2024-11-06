package dat.daos;

import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.TripCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TripDAO extends ITripGuideDAO implements IDAO<TripDTO, Integer> {

    private final EntityManagerFactory emf;

    public TripDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public TripDTO create(TripDTO dto) {
        EntityManager em = emf.createEntityManager();
        Trip trip = new Trip(dto);
        try {
            em.getTransaction().begin();
            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error creating Trip", e);
        } finally {
            em.close();
        }
    }

    public List<TripDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t", Trip.class);
            List<Trip> trips = query.getResultList();
            return trips.stream().map(TripDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all trips", e);
        } finally {
            em.close();
        }
    }


    // Add the populateDatabase method
    public void populateDatabase() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Sample data
            Guide guide1 = new Guide("Alice", "Johnson", "alice@example.com", "12345678", 5);
            Guide guide2 = new Guide("Bob", "Smith", "bob@example.com", "87654321", 10);

            Trip trip1 = new Trip(LocalDateTime.now(), LocalDateTime.now().plusDays(1), "Mountain Base", "Mountain Adventure", 200.0, TripCategory.MOUNTAIN, guide1);
            Trip trip2 = new Trip(LocalDateTime.now(), LocalDateTime.now().plusDays(2), "Beach", "Beach Relaxation", 150.0, TripCategory.BEACH, guide2);

            // Persist guides first if they're not already managed entities
            em.persist(guide1);
            em.persist(guide2);

            // Persist trips
            em.persist(trip1);
            em.persist(trip2);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error populating database with sample data", e);
        } finally {
            em.close();
        }
    }

    @Override
    public TripDTO getById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            Trip trip = em.find(Trip.class, id);
            return trip != null ? new TripDTO(trip) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Trip by ID", e);
        } finally {
            em.close();
        }
    }


    @Override
    public TripDTO update(Integer id, TripDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            Trip trip = em.find(Trip.class, id);
            if (trip != null) {
                em.getTransaction().begin();
                trip.setName(dto.getName());
                trip.setStartTime(dto.getStartTime());
                trip.setEndTime(dto.getEndTime());
                trip.setStartPosition(dto.getStartPosition());
                trip.setPrice(dto.getPrice());
                trip.setCategory(dto.getCategory());
                em.getTransaction().commit();
                return new TripDTO(trip);
            }
            return null;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error updating Trip", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            Trip trip = em.find(Trip.class, id);
            if (trip != null) {
                em.getTransaction().begin();
                em.remove(trip);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error deleting Trip", e);
        } finally {
            em.close();
        }
    }

    /**
     * Adds a guide to a specific trip.
     */
    public void addGuideToTrip(int tripId, int guideId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, tripId);
            Guide guide = em.find(Guide.class, guideId);
            if (trip != null && guide != null) {
                trip.setGuide(guide); // Set the guide to the trip
                em.merge(trip); // Persist the changes
                em.getTransaction().commit();
            } else {
                throw new RuntimeException("Guide or Trip not found");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error adding Guide to Trip", e);
        } finally {
            em.close();
        }
    }

    public Set<TripDTO> getTripsByGuide(int guideId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.guide.id = :guideId", Trip.class);
            query.setParameter("guideId", guideId);
            List<Trip> trips = query.getResultList();
            return trips.stream().map(TripDTO::new).collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving trips for guide with ID " + guideId, e);
        } finally {
            em.close();
        }
    }
}
