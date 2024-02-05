package rs.raf.springusers.services.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rs.raf.springusers.dto.VacuumRequest;
import rs.raf.springusers.entities.Status;
import rs.raf.springusers.entities.User;
import rs.raf.springusers.entities.Vacuum;
import rs.raf.springusers.exceptions.VacuumOperationException;
import rs.raf.springusers.repository.UserRepository;
import rs.raf.springusers.repository.VacuumRepository;
import rs.raf.springusers.services.VacuumService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class VacuumServiceImpl implements VacuumService {
    @Autowired
    private VacuumRepository vacuumRepository;
    @Autowired
    private UserRepository  userRepository;
    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    public List<Vacuum> findVacuumsByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        return vacuumRepository.findByCreatedAtBetween(fromDate, toDate);
    }

    @Override
    public Vacuum addVacuum(Long id,VacuumRequest vacuumRequest) {
        User user = userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("User not found"));
        Vacuum vacuum = new Vacuum();
        vacuum.setName(vacuumRequest.getName());
        vacuum.setStatus(Status.OFF);
        vacuum.setActive(vacuumRequest.getActive());
        vacuum.setUser(user);

        user.getVacuums().add(vacuum);
        userRepository.save(user);
        return vacuum;
    }
    @Override
    public List<Vacuum> searchVacuumsByName(String name, int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return vacuumRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    @Transactional
    @Async
    public CompletableFuture<Void> turnOnVacuumAsync(Long vacuumId) throws VacuumOperationException,EntityNotFoundException{
        Vacuum vacuum = vacuumRepository.findById(vacuumId).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
        if(vacuum.getStatus()!=Status.OFF)
        {
            throw new VacuumOperationException("The vacuum is not turned off");
        }
        try {
            CompletableFuture<Void> delayedFuture = new CompletableFuture<>();
            CompletableFuture.delayedExecutor(15, TimeUnit.SECONDS)
                    .execute(() -> {
                        // Set the status to ON
                        vacuum.setStatus(Status.ON);
                        vacuum.getDischargeCounter().incrementAndGet();
                        vacuumRepository.save(vacuum);
                        delayedFuture.complete(null);
                    });

            return delayedFuture;
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Transactional
    @Async
    public CompletableFuture<Void> turnOffVacuumAsync(Long vacuumId) throws VacuumOperationException,EntityNotFoundException{
        Vacuum vacuum = vacuumRepository.findById(vacuumId).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
        if(vacuum.getStatus()!=Status.ON)
        {
            throw new VacuumOperationException("The vacuum is not turned on");
        }
        try {
            CompletableFuture<Void> delayedFuture = new CompletableFuture<>();
            CompletableFuture.delayedExecutor(15, TimeUnit.SECONDS)
                    .execute(() -> {
                        // Set the status to ON
                        if(vacuum.getDischargeCounter().get() % 3 == 0)
                        {
                            vacuum.setStatus(Status.OFF);
                            vacuumRepository.save(vacuum);


                            try {
                                dischargeVacuumAsync(vacuumId);
                            } catch (VacuumOperationException e) {
                                throw new RuntimeException("Discharge exception");
                            }


                            vacuum.getDischargeCounter().set(0);
                            delayedFuture.complete(null);
                        }else {
                            vacuum.setStatus(Status.OFF);
                            vacuumRepository.save(vacuum);
                            delayedFuture.complete(null);
                        }
                    });

            return delayedFuture;
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Transactional
    @Async
    public CompletableFuture<Void> dischargeVacuumAsync(Long vacuumId) throws VacuumOperationException,EntityNotFoundException {
        Vacuum vacuum = vacuumRepository.findById(vacuumId).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
        if(vacuum.getStatus()!=Status.OFF)
        {
            throw new VacuumOperationException("The vacuum is not turned off");
        }

        try {
            CompletableFuture<Void> delayedFuture = new CompletableFuture<>();
            CompletableFuture.delayedExecutor(15, TimeUnit.SECONDS)
                    .execute(() -> {
                        // Set the status to DISCHARGE after 15 seconds
                        vacuum.setStatus(Status.DISCHARGE);
                        vacuumRepository.save(vacuum);
                    });

            CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS)
                    .execute(() -> {
                        // Set the status back to OFF after a total of 30 seconds
                        vacuum.setStatus(Status.OFF);
                        vacuumRepository.save(vacuum);
                        delayedFuture.complete(null);
                    });

            return delayedFuture;
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public Vacuum findById(Long vacuumId) throws EntityNotFoundException{
        return vacuumRepository.findById(vacuumId).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
    }
    @Async
    @Transactional
    @Override
    public void turnOnScheduled(Long id, String dateTime) throws VacuumOperationException,EntityNotFoundException{
        Instant instant = stringToInstant(dateTime);
        taskScheduler.schedule(()-> {
            try {
                turnOnVacuumAsync(id);
            } catch (VacuumOperationException e) {
                throw new RuntimeException("Turn on scheduled exception");
            }
        }, instant);
    }
    @Async
    @Transactional
    @Override
    public void turnOffScheduled(Long id, String dateTime)  throws VacuumOperationException,EntityNotFoundException{

            Instant instant = stringToInstant(dateTime);
            taskScheduler.schedule(() -> {
                try {
                    turnOffVacuumAsync(id);
                } catch (VacuumOperationException e) {
                    throw new RuntimeException("Turn off scheduled exception");
                }
            }, instant);

        }

    @Async
    @Transactional
    @Override
    public void dischargeScheduled(Long id, String dateTime) throws VacuumOperationException,EntityNotFoundException{

        Instant instant = stringToInstant(dateTime);
        taskScheduler.schedule(()-> {
            try {
                dischargeVacuumAsync(id);
            } catch (VacuumOperationException e) {
                throw new RuntimeException("Discharge scheduled exception");
            }
        }, instant);
    }

    @Override
    public List<Vacuum> getAllVacuums(Long id) {
        List<Vacuum> vacuums = vacuumRepository.findByUserId(id);
        if(vacuums==null) throw new EntityNotFoundException("User has no vacuums");
        return vacuums;
    }

    @Override
    public void deactivateVacuum(Long id) {
        Vacuum vacuum = vacuumRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
        vacuum.setActive(1);
        this.vacuumRepository.save(vacuum);
    }

    @Override
    public List<Vacuum> searchVacuumByStatus(List<Status> status) {
        List<Vacuum> toReturn = new ArrayList<>();
        for (Status s:status)
        {
            List<Vacuum> vacuums = this.vacuumRepository.findByStatus(s);
            toReturn.addAll(vacuums);
        }
        return toReturn;
    }

    private Instant stringToInstant(String dateTime)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime ld =  LocalDateTime.parse(dateTime, formatter);
        Instant instant = ld.atZone(ZoneId.systemDefault()).toInstant();
        return instant;
    }
}
