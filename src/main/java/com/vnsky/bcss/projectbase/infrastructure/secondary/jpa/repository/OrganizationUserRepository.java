package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.OrganizationUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUserEntity, String>{
    Optional<OrganizationUserEntity> findByUserId(String userId);
}
