package dat.daos;

import dat.dtos.GuideDTO;
import dat.entities.Guide;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

public class GuideDAO implements IDAO<GuideDTO, Integer> {

    private final EntityManagerFactory emf;

    public GuideDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public GuideDTO create(GuideDTO dto) {
        EntityManager em = emf.createEntityManager();
        Guide guide = new Guide(dto);
        try {
            em.getTransaction().begin();
            em.persist(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error creating Guide", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<GuideDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Guide> query = em.createQuery("SELECT g FROM Guide g", Guide.class);
            List<Guide> guides = query.getResultList();
            return guides.stream().map(GuideDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all guides", e);
        } finally {
            em.close();
        }
    }

    @Override
    public GuideDTO getById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = em.find(Guide.class, id);
            return guide != null ? new GuideDTO() : null;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Guide by ID", e);
        } finally {
            em.close();
        }
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = em.find(Guide.class, id);
            if (guide != null) {
                em.getTransaction().begin();
                guide.setFirstname(dto.getFirstName());
                guide.setLastname(dto.getLastName());
                guide.setEmail(dto.getEmail());
                guide.setPhone(dto.getPhone());
                guide.setYearsOfExperience(dto.getYearsOfExperience());
                em.getTransaction().commit();
                return new GuideDTO();
            }
            return null;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error updating Guide", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = em.find(Guide.class, id);
            if (guide != null) {
                em.getTransaction().begin();
                em.remove(guide);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error deleting Guide", e);
        } finally {
            em.close();
        }
    }
}
