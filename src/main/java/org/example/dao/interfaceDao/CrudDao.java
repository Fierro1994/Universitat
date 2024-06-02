package org.example.dao.interfaceDao;

import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.ExistEntityException;
import java.util.Optional;
import java.util.Set;

public interface CrudDao<T> {

    Optional<T> getById(Long id);

    Set<T> getAll();

    void save(T t) throws ExistEntityException, EntityNotFoundException;

    void update(T t) throws EntityNotFoundException;

    void remove(T t) throws EntityNotFoundException;
}
