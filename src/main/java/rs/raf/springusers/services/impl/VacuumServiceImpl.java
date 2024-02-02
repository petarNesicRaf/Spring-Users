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
import rs.raf.springusers.repository.UserRepository;
import rs.raf.springusers.repository.VacuumRepository;
import rs.raf.springusers.services.VacuumService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public CompletableFuture<Void> turnOnVacuumAsync(Long vacuumId) {
        Vacuum vacuum = vacuumRepository.findById(vacuumId).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
        if(vacuum.getStatus()!=Status.OFF)
        {
            throw new EntityNotFoundException("The vacuum is not turned off");
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
    public CompletableFuture<Void> turnOffVacuumAsync(Long vacuumId) {
        Vacuum vacuum = vacuumRepository.findById(vacuumId).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
        if(vacuum.getStatus()!=Status.ON)
        {
            throw new EntityNotFoundException("The vacuum is not turned on");
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
                            dischargeVacuumAsync(vacuumId);
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
    public CompletableFuture<Void> dischargeVacuumAsync(Long vacuumId) {
        Vacuum vacuum = vacuumRepository.findById(vacuumId).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
        if(vacuum.getStatus()!=Status.OFF)
        {
            throw new EntityNotFoundException("The vacuum is not turned off");
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
    public Vacuum findById(Long vacuumId) {
        return vacuumRepository.findById(vacuumId).orElseThrow(()->new EntityNotFoundException("Vacuum not found"));
    }
    @Async
    @Transactional
    @Override
    public void turnOnScheduled(Long id, String dateTime) {
        Instant instant = stringToInstant(dateTime);
        taskScheduler.schedule(()->turnOnVacuumAsync(id), instant);
    }
    @Async
    @Transactional
    @Override
    public void turnOffScheduled(Long id, String dateTime) {
        Instant instant = stringToInstant(dateTime);
        taskScheduler.schedule(()->turnOffVacuumAsync(id), instant);
    }
    @Async
    @Transactional
    @Override
    public void dischargeScheduled(Long id, String dateTime) {

        Instant instant = stringToInstant(dateTime);
        taskScheduler.schedule(()->dischargeVacuumAsync(id), instant);
    }

    private Instant stringToInstant(String dateTime)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime ld =  LocalDateTime.parse(dateTime, formatter);
        Instant instant = ld.atZone(ZoneId.systemDefault()).toInstant();
        return instant;
    }
}
