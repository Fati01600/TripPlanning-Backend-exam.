package dat.daos;

import java.util.List;

public interface IDAO<T, K> {
    T create(T dto);
    List<T> getAll();
    T getById(K id);
    T update(K id, T dto);
    boolean delete(K id);
}
