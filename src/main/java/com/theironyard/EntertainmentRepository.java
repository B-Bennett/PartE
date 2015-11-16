package com.theironyard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by BennettIronYard on 11/13/15.
 */
public interface EntertainmentRepository extends CrudRepository<Entertainment, Integer> {
    List<Entertainment> findByType(String type);
    List<Entertainment> findByTypeAndPrice(String type, Integer price);

    Entertainment findFirstByType(String type);
    int countByType(String type);
    List<Entertainment> findByTypeOrderByNameAsc(String type);

    @Query("SELECT e FROM Entertainment e WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'")
    List<Entertainment> searchByName(String name);

}
