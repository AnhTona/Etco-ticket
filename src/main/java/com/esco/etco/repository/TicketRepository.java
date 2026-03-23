package com.esco.etco.repository;

import com.esco.etco.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long>, JpaSpecificationExecutor<Ticket> {
    @Query("SELECT t FROM Ticket t WHERE LOWER(t.event.name) LIKE LOWER(CONCAT('%', :eventName, '%'))")
    List<Ticket> searchTicketsByEventName(@Param("eventName") String eventName);
}
