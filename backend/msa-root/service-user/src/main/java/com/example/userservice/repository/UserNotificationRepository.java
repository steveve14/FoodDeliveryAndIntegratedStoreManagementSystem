package com.example.userservice.repository;

import com.example.userservice.entity.UserNotification;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** UserNotificationRepository 타입입니다. */
@Repository
public interface UserNotificationRepository extends CrudRepository<UserNotification, Long> {

  /** 사용자 알림 목록을 최신순으로 조회합니다. */
  List<UserNotification> findAllByOrderByCreatedAtDesc();
}
