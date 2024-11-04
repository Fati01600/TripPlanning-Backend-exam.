/*package dat;

import dat.entities.Plant;
import dat.entities.Reseller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.*;

public class PopulatorTest {

    public Set<Reseller> populateResellers(EntityManagerFactory emf)
    {
        Set<Reseller> resellers = new HashSet<>();

        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            Reseller reseller1 = new Reseller("Lyngby Plantecenter", "Firskovvej 18", "33212334");
            Reseller reseller2 = new Reseller("Glostrup Planter", "Tværvej 35", "32233232");
            Reseller reseller3 = new Reseller("Holbæk Planteskole", "Stenhusvej 49", "59430945");

            reseller1.setPlants(getPlantsLyngby(reseller1));
            reseller2.setPlants(getPlantsGlostrup(reseller2));
            reseller3.setPlants(getPlantsHolbæk(reseller3));

            resellers.add(reseller1);
            resellers.add(reseller2);
            resellers.add(reseller3);

            em.persist(reseller1);
            em.persist(reseller2);
            em.persist(reseller3);

            em.getTransaction().commit();
        }
        return resellers;
    }


    public Set<Plant> populatePlants (EntityManagerFactory emf){

         Set<Plant> plants = new HashSet<>();

         try (EntityManager em = emf.createEntityManager())
         {
              em.getTransaction().begin();

             Reseller reseller1 = new Reseller("Lyngby Plantecenter", "Firskovvej 18", "33212334");
             Reseller reseller2 = new Reseller("Glostrup Planter", "Tværvej 35", "32233232");
             Reseller reseller3 = new Reseller("Holbæk Planteskole", "Stenhusvej 49", "59430945");

             Set<Plant> plantsLyngby = getPlantsLyngby(reseller1);
             Set<Plant> plantsGlostrup = getPlantsGlostrup(reseller2);
             Set<Plant> plantsHolbæk = getPlantsHolbæk(reseller3);

             em.persist(reseller1);
             em.persist(reseller2);
             em.persist(reseller3);

             // Persist each plant in their respective list
             for (Plant plant : plantsLyngby) {
                 em.persist(plant);
             }
             for (Plant plant : plantsGlostrup) {
                 em.persist(plant);
             }
             for (Plant plant : plantsHolbæk) {
                 em.persist(plant);
             }

             // Add plants to the result list
             plants.addAll(plantsLyngby);
             plants.addAll(plantsGlostrup);
             plants.addAll(plantsHolbæk);

              em.getTransaction().commit();
         }
         return plants;

   }

    private Set<Plant> getPlantsHolbæk(Reseller reseller3) {
        Set<Plant> plants = new HashSet<>();

        Plant plant1 = new Plant("Albertine", "Rose", 400, 199.50);
        Plant plant2 = new Plant("Aronia", "Bush", 200, 169.50);
        Plant plant3 = new Plant("AromaApple", "FruitAndBerries", 350, 399.50);

        plant1.addReseller(reseller3);
        plant2.addReseller(reseller3);
        plant3.addReseller(reseller3);

        plants.add(plant1);
        plants.add(plant2);
        plants.add(plant3);

        return plants;
    }

    private Set<Plant> getPlantsGlostrup(Reseller reseller2) {
        Set<Plant> plants = new HashSet<>();

        Plant plant2 = new Plant("Aronia", "Bush", 200, 169.50);
        Plant plant3 = new Plant("AromaApple", "FruitAndBerries", 350, 399.50);
        Plant plant4 = new Plant("Astrid", "Rhododendron", 40, 269.50);

        plant2.addReseller(reseller2);
        plant3.addReseller(reseller2);
        plant4.addReseller(reseller2);

        plants.add(plant2);
        plants.add(plant3);
        plants.add(plant4);

        return plants;
    }

    private Set<Plant> getPlantsLyngby(Reseller reseller1) {
        Set<Plant> plants = new HashSet<>();

        Plant plant3 = new Plant("AromaApple", "FruitAndBerries", 350, 399.50);
        Plant plant4 = new Plant("Astrid", "Rhododendron", 40, 269.50);
        Plant plant5 = new Plant("The DarkLady", "Rose", 100, 199.50);

        plant3.addReseller(reseller1);
        plant4.addReseller(reseller1);
        plant5.addReseller(reseller1);

        plants.add(plant3);
        plants.add(plant4);
        plants.add(plant5);

        return plants;
    }

    public void cleanUpData(EntityManagerFactory emf)
    {
        // Delete all data from database
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Plant").executeUpdate();
            em.createQuery("DELETE FROM Reseller ").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE plant_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE reseller_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    // Kan også bruges som populator til test og matcher lidt mere med populate metoden i Populate.java.
    Er ikke prøvet så kan nok godt skabe problemer.
    public Set<Reseller> populateResellers(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Reseller reseller1 = new Reseller("Lyngby Plantecenter", "Firskovvej 18", "33212334");
        Reseller reseller2 = new Reseller("Glostrup Planter", "Tværvej 35", "32233232");
        Reseller reseller3 = new Reseller("Holbæk Planteskole", "Stenhusvej 49", "59430945");

        em.persist(reseller1);
        em.persist(reseller2);
        em.persist(reseller3);
        em.getTransaction().commit();
        em.close();

        return new HashSet<>(Arrays.asList(reseller1, reseller2, reseller3));
    }

    public Set<Plant> populatePlants(EntityManagerFactory emf, Set<Reseller> resellers) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Plant plant1 = new Plant("Albertine", "Rose", 400, 199.50);
        Plant plant2 = new Plant("Aronia", "Bush", 200, 169.50);
        Plant plant3 = new Plant("AromaApple", "FruitAndBerries", 350, 399.50);
        Plant plant4 = new Plant("Astrid", "Rhododendron", 40, 269.50);
        Plant plant5 = new Plant("The DarkLady", "Rose", 100, 199.50);

        em.persist(plant1);
        em.persist(plant2);
        em.persist(plant3);
        em.persist(plant4);
        em.persist(plant5);

        // Set relationships between plants and resellers
        plant1.setResellers(new HashSet<>(Arrays.asList(findResellerByName(resellers, "Lyngby Plantecenter"), findResellerByName(resellers, "Holbæk Planteskole"))));
        plant2.setResellers(new HashSet<>(Arrays.asList(findResellerByName(resellers, "Lyngby Plantecenter"), findResellerByName(resellers, "Glostrup Planter"))));
        plant3.setResellers(new HashSet<>(Arrays.asList(findResellerByName(resellers, "Glostrup Planter"), findResellerByName(resellers, "Holbæk Planteskole"))));
        plant4.setResellers(new HashSet<>(Arrays.asList(findResellerByName(resellers, "Glostrup Planter"), findResellerByName(resellers, "Lyngby Plantecenter"))));
        plant5.setResellers(new HashSet<>(Arrays.asList(findResellerByName(resellers, "Holbæk Planteskole"), findResellerByName(resellers, "Lyngby Plantecenter"))));

        em.merge(plant1);
        em.merge(plant2);
        em.merge(plant3);
        em.merge(plant4);
        em.merge(plant5);
        em.getTransaction().commit();
        em.close();

        return new HashSet<>(Arrays.asList(plant1, plant2, plant3, plant4, plant5));
    }*/
