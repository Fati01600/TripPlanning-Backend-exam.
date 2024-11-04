package dat.security.daos;
import dat.security.entities.Role;
import dat.security.entities.User;
import dat.security.exceptions.ApiException;
import dat.security.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityDAO implements ISecurityDAO {

    private static ISecurityDAO instance;
    private static EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<UserDTO> getListOfUsers(){
        List<UserDTO> userDTOList = new ArrayList<>();
        try (EntityManager em = getEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            for (User u: query.getResultList()){
                userDTOList.add(new UserDTO(u.getUsername(), u.getPassword(), u.getRolesAsStrings()));
            }
            return userDTOList;
        }
    }


    public UserDTO getByUsername(String username) {
        try (EntityManager em = emf.createEntityManager())
        {
            User user = em.find(User.class, username);
            return new UserDTO(user.getUsername(), user.getPassword(), user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
        }
    }


    @Override
    public UserDTO getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = getEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
            user.getRoles().size(); // force roles to be fetched from db
            if (!user.verifyPassword(password))
                throw new ValidationException("Wrong password");
            return new UserDTO(user.getUsername(), user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
        }
    }

    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = getEntityManager()) {
            User userEntity = em.find(User.class, username);
            if (userEntity != null)
                throw new EntityExistsException("User with username: " + username + " already exists");
            userEntity = new User(username, password);
            em.getTransaction().begin();
            Role userRole = em.find(Role.class, "user");
            if (userRole == null)
            {
                userRole = new Role("user");
                em.persist(userRole);
            }
            userEntity.addRole(userRole);
            em.persist(userEntity);
            em.getTransaction().commit();
            return userEntity;
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public User addRole(UserDTO userDTO, String newRole) {
        try (EntityManager em = getEntityManager()) {
            User user = em.find(User.class, userDTO.getUsername());
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + userDTO.getUsername());
            em.getTransaction().begin();

                // Fjern alle eksisterende roller
                user.getRoles().clear();
                Role role = em.find(Role.class, newRole);
                if (role == null) {
                    role = new Role(newRole);
                    em.persist(role);
                }
                user.addRole(role);
                //em.merge(user);
            em.getTransaction().commit();
            return user;
        }
    }

    //Vi kan ikke opdatere username, da den er primærnøgle. men password og roller kan opdateres.
    public UserDTO updateUser(String username, UserDTO userDTO){
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            User user = em.find(User.class, username);
            em.getTransaction();
            user.setUsername(userDTO.getUsername());

            // Update password if it's provided and use Bcrypt when storing the data.
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt()));
            }

            //konvertering fra Set<String> til Set<Role>
            Set<Role> roles = new HashSet<>();
            for (String roleName : userDTO.getRoles())
            {
                Role role = em.find(Role.class, roleName);
                if(role != null)
                {
                    roles.add(role);
                }
            }
            user.setRoles(roles);

            em.merge(user);
            em.getTransaction().commit();
            return new UserDTO(user.getUsername(), user.getPassword(), user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
        }
    }


    public void deleteUser(String username){
        try (EntityManager em = getEntityManager())
        {
            em.getTransaction().begin();
            User user = em.find(User.class, username);
            if(user != null)
            {
                em.remove(user);
            } else{
                throw new EntityNotFoundException("No user found with username: " + username);
            }
            em.getTransaction().commit();
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new ApiException(400, "Failed to delete user: " + e.getMessage());
        }
    }


    public boolean validatePrimaryKey(String username) {
        try (EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, username);
            return user != null;
        }
    }


//    Når man har sat roller på sine routes (som i vores getSecuredRoutes-metode), sørger frameworket allerede for,
//    at kun brugere med de rette rettigheder kan tilgå disse endpoints.
//    Derfor er ekstra autentifikation i DAO-metoderne overflødig.

//    nedenunder er validering af username og password i metoderne, før krævede det autentifikation og
//    password-validering for at hente, opdatere eller slette brugere.

//    User user = em.find(User.class, username);
//    if (user == null)
//    throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
//    user.getRoles().size(); // force roles to be fetched from db
//    if (!user.verifyPassword(password))
//    throw new ValidationException("Wrong password");

}

