package com.esco.etco.repository;

import com.esco.etco.entity.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Long>, JpaSpecificationExecutor<Event> {
    @Modifying
    @Query("UPDATE Event e SET e.isPublished = false WHERE e.isPublished = true AND e.startTime < :now")
    int unpublishExpiredEvents(@Param("now") Instant now);

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.images")
    List<Event> findAllWithImages();

    @EntityGraph(attributePaths = {"images"})
    Optional<Event> findEventById(long id);

    @Query("SELECT e FROM Event e WHERE e.endTime < :cutoff")
    List<Event> findEventsEndedBefore(@Param("cutoff") Instant cutoff);

    @Query("SELECT e FROM Event e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :eventName, '%'))")
    List<Event> searchEventsByName(@Param("eventName") String eventName);

    boolean existsByGenreId(long genreId);
}
