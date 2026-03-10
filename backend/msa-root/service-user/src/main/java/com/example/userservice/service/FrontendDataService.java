package com.example.userservice.service;

import com.example.userservice.dto.FrontendAvatarDto;
import com.example.userservice.dto.FrontendMailDto;
import com.example.userservice.dto.FrontendMemberDto;
import com.example.userservice.dto.FrontendNotificationDto;
import com.example.userservice.dto.FrontendUserDto;
import com.example.userservice.entity.MailMessage;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserNotification;
import com.example.userservice.repository.MailMessageRepository;
import com.example.userservice.repository.UserNotificationRepository;
import com.example.userservice.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class FrontendDataService {

  private final UserRepository userRepository;
  private final MailMessageRepository mailMessageRepository;
  private final UserNotificationRepository userNotificationRepository;

  public FrontendDataService(
      UserRepository userRepository,
      MailMessageRepository mailMessageRepository,
      UserNotificationRepository userNotificationRepository) {
    this.userRepository = userRepository;
    this.mailMessageRepository = mailMessageRepository;
    this.userNotificationRepository = userNotificationRepository;
  }

  public List<FrontendUserDto> getCustomers() {
    return userRepository.findByRolesOrderByCreatedAtDesc("USER").stream().map(this::toFrontendUser).toList();
  }

  public List<FrontendMemberDto> getMembers() {
    return userRepository.findByRolesAndTeamRoleIsNotNullOrderByCreatedAtDesc("ADMIN").stream()
        .map(
            user ->
                new FrontendMemberDto(
                    user.getName(),
                    user.getUsername(),
                    user.getTeamRole(),
                    new FrontendAvatarDto(user.getAvatarUrl())))
        .toList();
  }

  public List<FrontendMailDto> getMails() {
    List<MailMessage> mails = mailMessageRepository.findAllByOrderByCreatedAtDesc();
    Map<String, User> userMap = loadUsers(mails.stream().map(MailMessage::getFromUserId).toList());

    return mails.stream()
        .map(
            mail ->
                new FrontendMailDto(
                    mail.getId(),
                    mail.isUnread(),
                    toFrontendUser(userMap.get(mail.getFromUserId())),
                    mail.getSubject(),
                    mail.getBody(),
                    mail.getCreatedAt().toString()))
        .toList();
  }

  public List<FrontendNotificationDto> getNotifications() {
    List<UserNotification> notifications = userNotificationRepository.findAllByOrderByCreatedAtDesc();
    Map<String, User> userMap = loadUsers(notifications.stream().map(UserNotification::getUserId).toList());

    return notifications.stream()
        .map(
            notification ->
                new FrontendNotificationDto(
                    notification.getId(),
                    notification.isUnread(),
                    toFrontendUser(userMap.get(notification.getUserId())),
                    notification.getBody(),
                    notification.getCreatedAt().toString()))
        .toList();
  }

  private Map<String, User> loadUsers(List<String> userIds) {
    Map<String, User> userMap = new HashMap<>();
    userRepository.findAllById(userIds).forEach(user -> userMap.put(user.getId(), user));
    return userMap;
  }

  private FrontendUserDto toFrontendUser(User user) {
    if (user == null) {
      return new FrontendUserDto(
          "unknown",
          "알 수 없음",
          "unknown@foodplatform.local",
          new FrontendAvatarDto("https://i.pravatar.cc/128?u=fdms-unknown"),
          "unsubscribed",
          "미확인");
    }

    return new FrontendUserDto(
        user.getId(),
        user.getName(),
        user.getEmail(),
        new FrontendAvatarDto(user.getAvatarUrl()),
        user.getMarketingStatus() == null ? "subscribed" : user.getMarketingStatus(),
        user.getLocation() == null ? "미설정" : user.getLocation());
  }
}