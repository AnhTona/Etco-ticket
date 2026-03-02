package com.esco.etco.repository;

import com.esco.etco.entity.UserTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTicketRepository extends JpaRepository<UserTicket, Long>, JpaSpecificationExecutor<UserTicket> {
    Optional<UserTicket> findByQrCode(String qrCode);
    List<UserTicket> findByUserId(long userId);
    List<UserTicket> findByEventId(long eventId);
    List<UserTicket> findByOrderId(long orderId);
}