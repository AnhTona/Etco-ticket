package com.esco.etco.repository;

import com.esco.etco.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long> {
    @Modifying
    @Query("UPDATE EventImage img SET img.isCover = false WHERE img.event.id = :eventId")
    void clearCoverByEventId(@Param("eventId") long eventId);

    List<EventImage> findByEventId(long eventId);

    List<EventImage> findByEventIdIn(List<Long> eventIds);

    Optional<EventImage> findByIdAndEventId(long id, long eventId);

    @Modifying
    @Query("DELETE FROM EventImage img WHERE img.event.id = :eventId")
    void deleteAllByEventId(@Param("eventId") long eventId);
}
