package rs.raf.springusers.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.springusers.entities.Status;
import rs.raf.springusers.entities.Vacuum;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VacuumRepository extends JpaRepository<Vacuum,Long> {
    List<Vacuum> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Vacuum> findByUserId(Long id);
    List<Vacuum> findByStatus(Status status);
    @Query("SELECT v FROM Vacuum v WHERE v.createdAt BETWEEN :fromDate AND :toDate")
    List<Vacuum> findByCreatedAtBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}

