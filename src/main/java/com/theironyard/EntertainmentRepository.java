package com.theironyard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by BennettIronYard on 11/13/15.
 */
public interface EntertainmentRepository extends PagingAndSortingRepository<Entertainment, Integer> {
    Page<Entertainment> findByType(Pageable pageable, String type);
    Page<Entertainment> findByPrice(Pageable pageable, Integer price);
    Page<Entertainment> findByName(Pageable pageable, String name);
}
