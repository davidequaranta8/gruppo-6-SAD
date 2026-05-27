package group6.java.group6.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/*T stands for the entity , while K stands for the unique identifier of that entity*/
public interface Dao <T,K> {

    /*Used to get a record with a specific key*/
    Optional<T> get(K key);

    /*Get all entities*/
    Set<T> getAll();

    /*Save an entity in db*/
    void save(T t);

    /*Delete an entity in db*/
    void delete(T t);

    /*Update an entity in db , you have to pass the object already modified*/
    void update(T t);







}
