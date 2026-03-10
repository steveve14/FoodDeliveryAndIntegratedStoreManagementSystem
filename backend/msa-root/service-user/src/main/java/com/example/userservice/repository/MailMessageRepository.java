package com.example.userservice.repository;

import com.example.userservice.entity.MailMessage;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailMessageRepository extends CrudRepository<MailMessage, Long> {

	List<MailMessage> findAllByOrderByCreatedAtDesc();
}