package org.example.Dao.interfaceDao;

import java.util.Optional;
import java.util.Set;

public interface CrudDao<T> {
    Optional<T> getById(Long id);

    Set<T> getAll();

    void save(T t);

    void update(T t);

    void remove(T t);
}
