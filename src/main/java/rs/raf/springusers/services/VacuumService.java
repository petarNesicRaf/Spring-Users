package rs.raf.springusers.services;

import rs.raf.springusers.dto.VacuumRequest;
import rs.raf.springusers.entities.Vacuum;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VacuumService {
    Vacuum addVacuum(Long id,VacuumRequest vacuumRequest);
    List<Vacuum> searchVacuumsByName(String name, int pageSize);
    CompletableFuture<Void> turnOnVacuumAsync(Long vacuumId);
    CompletableFuture<Void> turnOffVacuumAsync(Long vacuumId);
    CompletableFuture<Void> dischargeVacuumAsync(Long vacuumId);
    Vacuum findById(Long vacuumId);

    void turnOnScheduled(Long id, String dateTime);
    void turnOffScheduled(Long id, String dateTime);
    void dischargeScheduled(Long id, String dateTime);

}
