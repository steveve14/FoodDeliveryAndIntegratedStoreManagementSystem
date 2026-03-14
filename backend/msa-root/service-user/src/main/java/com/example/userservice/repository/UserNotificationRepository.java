package com.example.userservice.repository;

import com.example.userservice.entity.UserNotification;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** UserNotificationRepository 타입입니다. */
@Repository
public interface UserNotificationRepository extends CrudRepository<UserNotification, Long> {

  List<UserNotification> findAllByOrderByCreatedAtDesc();
}
