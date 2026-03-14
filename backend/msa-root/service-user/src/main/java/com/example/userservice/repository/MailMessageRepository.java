package com.example.userservice.repository;

import com.example.userservice.entity.MailMessage;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** MailMessageRepository 타입입니다. */
@Repository
public interface MailMessageRepository extends CrudRepository<MailMessage, Long> {

  /** 메일 메시지 목록을 최신순으로 조회합니다. */
  List<MailMessage> findAllByOrderByCreatedAtDesc();
}
