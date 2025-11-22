package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.RoomPaymentDetailEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomPaymentDetailRepository extends BaseJPARepository<RoomPaymentDetailEntity, String> {

    List<RoomPaymentDetailEntity> findByRoomPaymentIdOrderByCreatedDateAsc(String roomPaymentId);

    @Modifying
    @Query("DELETE FROM RoomPaymentDetailEntity rpd WHERE rpd.roomPaymentId = :roomPaymentId")
    void deleteByRoomPaymentId(@Param("roomPaymentId") String roomPaymentId);
}

