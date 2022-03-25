package repository;

import domain.Entity;
import exceptions.RepositoryException;

public interface Repo<ID, E extends Entity<ID>> {

    /**
     *
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     *
     * @param entity
     * entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws RepositoryException
     * if the entity is not valid
     * @throws IllegalArgumentException
     * if the given entity is null. *
     */
    E save(E entity);

    /**
     * removes the entity with the specified id
     * @param id
     * id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException
     * if the given id is null.
     */
    E delete(ID id);

    /**
     *
     * @param entity
     * entity must not be null
     * @return null - if the entity is updated,
     * otherwise returns the entity - (e.g id does not exist).
     * @throws IllegalArgumentException
     * if the given entity is null.
     * @throws RepositoryException
     * if the entity is not valid.
     */
    E update(E entity);

}