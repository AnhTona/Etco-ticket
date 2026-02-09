package com.esco.etco.repository;

import com.esco.etco.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long> {
    @Modifying
    @Query("UPDATE EventImage img SET img.isCover = false WHERE img.event.id = :eventId")
    void clearCoverByEventId(@Param("eventId") long eventId);

    List<EventImage> findByEventId(long eventId);

    List<EventImage> findByEventIdIn(List<Long> eventIds);
}
