package com.example.userservice.service;

import com.example.userservice.dto.AddressDto;
import com.example.userservice.entity.Address;
import com.example.userservice.repository.AddressRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** AddressService 타입입니다. */
@Service
public class AddressService {
  private final AddressRepository addressRepository;

  /** 주소 서비스를 생성합니다. */
  public AddressService(AddressRepository addressRepository) {
    this.addressRepository = addressRepository;
  }

  /** 사용자 주소 목록을 조회합니다. */
  public List<AddressDto> listByUser(String userId) {
    return addressRepository.findByUserId(userId).stream()
        .map(
            a ->
                new AddressDto(
                    a.getId(),
                    a.getUserId(),
                    a.getLabel(),
                    a.getLine1(),
                    a.getLine2(),
                    a.getCity(),
                    a.getState(),
                    a.getPostalCode(),
                    a.getCountry(),
                    a.isPrimaryAddress(),
                    a.getCreatedAt()))
        .collect(Collectors.toList());
  }

  /** 사용자 주소를 생성합니다. */
  @Transactional
  public AddressDto create(String userId, AddressDto req) {
    Address a =
        Address.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .label(req.getLabel())
            .line1(req.getLine1())
            .line2(req.getLine2())
            .city(req.getCity())
            .state(req.getState())
            .postalCode(req.getPostalCode())
            .country(req.getCountry())
            .primaryAddress(req.isPrimaryAddress())
            .createdAt(Instant.now())
            .isNewEntity(true)
            .build();
    Address saved = addressRepository.save(a);
    return new AddressDto(
        saved.getId(),
        saved.getUserId(),
        saved.getLabel(),
        saved.getLine1(),
        saved.getLine2(),
        saved.getCity(),
        saved.getState(),
        saved.getPostalCode(),
        saved.getCountry(),
        saved.isPrimaryAddress(),
        saved.getCreatedAt());
  }

  /** 사용자 주소를 수정합니다. */
  @Transactional
  public AddressDto update(String userId, String addressId, AddressDto req) {
    Address existing =
        addressRepository
            .findById(addressId)
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));
    if (!existing.getUserId().equals(userId)) {
      throw new IllegalArgumentException("Forbidden");
    }
    Address updated =
        existing.toBuilder()
            .label(req.getLabel())
            .line1(req.getLine1())
            .line2(req.getLine2())
            .city(req.getCity())
            .state(req.getState())
            .postalCode(req.getPostalCode())
            .country(req.getCountry())
            .primaryAddress(req.isPrimaryAddress())
            .build();
    Address saved = addressRepository.save(updated);
    return new AddressDto(
        saved.getId(),
        saved.getUserId(),
        saved.getLabel(),
        saved.getLine1(),
        saved.getLine2(),
        saved.getCity(),
        saved.getState(),
        saved.getPostalCode(),
        saved.getCountry(),
        saved.isPrimaryAddress(),
        saved.getCreatedAt());
  }

  /** 사용자 주소를 삭제합니다. */
  @Transactional
  public void delete(String userId, String addressId) {
    Address existing =
        addressRepository
            .findById(addressId)
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));
    if (!existing.getUserId().equals(userId)) {
      throw new IllegalArgumentException("Forbidden");
    }
    addressRepository.deleteById(addressId);
  }
}
