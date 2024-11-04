package dat.config;

import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.TripCategory;
import dat.security.entities.Role;
import dat.security.entities.User;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;


public class Populate {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Create Roles
            Role adminRole = new Role("ADMIN");
            Role userRole = new Role("USER");
            em.persist(adminRole);
            em.persist(userRole);

            // Create Users with Roles
            User u1 = new User("john", "password123");
            User u2 = new User("Gitte", "gitte123");
            User u3 = new User("Finn", "finn123");
            User u4 = new User("Lars", "lars123");

            u1.addRole(userRole);
            u2.addRole(userRole);
            u3.addRole(userRole);
            u4.addRole(adminRole);

            em.persist(u1);
            em.persist(u2);
            em.persist(u3);
            em.persist(u4);

            // Create sample Guides
            Guide guide1 = new Guide("Alice", "Smith", "alice@example.com", "12345678", 5);
            Guide guide2 = new Guide("Bob", "Johnson", "bob@example.com", "87654321", 8);
            Guide guide3 = new Guide("Clara", "Lee", "clara@example.com", "99887766", 3);

            em.persist(guide1);
            em.persist(guide2);
            em.persist(guide3);

            // Create sample Trips and assign Guides
            Trip trip1 = new Trip(LocalDateTime.now(), LocalDateTime.now().plusDays(1), "City Center", "City Tour", 100, TripCategory.CITY, guide1);
            Trip trip2 = new Trip(LocalDateTime.now(), LocalDateTime.now().plusDays(2), "Beach", "Beach Adventure", 150, TripCategory.BEACH, guide2);
            Trip trip3 = new Trip(LocalDateTime.now(), LocalDateTime.now().plusDays(3), "Forest Path", "Nature Walk", 120, TripCategory.FOREST, guide3);

            em.persist(trip1);
            em.persist(trip2);
            em.persist(trip3);

            // Establish relationships and persist changes
            guide1.getTrips().add(trip1);
            guide2.getTrips().add(trip2);
            guide3.getTrips().add(trip3);

            em.merge(guide1);
            em.merge(guide2);
            em.merge(guide3);

            em.getTransaction().commit();
        }
    }
}
