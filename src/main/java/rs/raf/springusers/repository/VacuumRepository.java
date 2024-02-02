package rs.raf.springusers.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.springusers.entities.Vacuum;

import java.util.List;

@Repository
public interface VacuumRepository extends JpaRepository<Vacuum,Long> {
    List<Vacuum> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
