package rs.raf.springusers.services;

import jakarta.persistence.EntityNotFoundException;
import rs.raf.springusers.dto.VacuumRequest;
import rs.raf.springusers.entities.Status;
import rs.raf.springusers.entities.Vacuum;
import rs.raf.springusers.exceptions.VacuumOperationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VacuumService {
    List<Vacuum> findVacuumsByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
    Vacuum addVacuum(Long id,VacuumRequest vacuumRequest);
    List<Vacuum> searchVacuumsByName(String name, int pageSize);
    CompletableFuture<Void> turnOnVacuumAsync(Long vacuumId) throws VacuumOperationException,EntityNotFoundException;
    CompletableFuture<Void> turnOffVacuumAsync(Long vacuumId) throws VacuumOperationException,EntityNotFoundException;
    CompletableFuture<Void> dischargeVacuumAsync(Long vacuumId) throws VacuumOperationException, EntityNotFoundException;
    Vacuum findById(Long vacuumId) throws EntityNotFoundException;

    void turnOnScheduled(Long id, String dateTime) throws VacuumOperationException,EntityNotFoundException;
    void turnOffScheduled(Long id, String dateTime) throws VacuumOperationException,EntityNotFoundException;
    void dischargeScheduled(Long id, String dateTime) throws VacuumOperationException,EntityNotFoundException;

    List<Vacuum> getAllVacuums(Long id);
    void deactivateVacuum(Long id);
    List<Vacuum> searchVacuumByStatus(List<Status> status);
}
