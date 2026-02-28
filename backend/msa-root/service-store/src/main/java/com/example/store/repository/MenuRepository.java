package com.example.store.repository;

import com.example.store.entity.Menu;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
/**
 * Repository for Menu entities.
 */
public interface MenuRepository extends CrudRepository<Menu, String> {

  List<Menu> findByStoreId(String storeId);
}
