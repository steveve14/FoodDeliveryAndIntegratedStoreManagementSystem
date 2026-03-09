package com.example.store.repository;

import com.example.store.entity.Store;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/** Repository for Store entities. */
public interface StoreRepository extends CrudRepository<Store, String> {
    Optional<Store> findByOwnerId(String ownerId);
}
