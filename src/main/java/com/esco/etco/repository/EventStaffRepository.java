package com.esco.etco.repository;

import com.esco.etco.entity.EventStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventStaffRepository extends JpaRepository<EventStaff, Long> {
    List<EventStaff> findByEventId(long eventId);
    List<EventStaff> findByUserId(long userId);
    Optional<EventStaff> findByEventIdAndUserId(long eventId, long userId);
    boolean existsByEventIdAndUserId(long eventId, long userId);
}
