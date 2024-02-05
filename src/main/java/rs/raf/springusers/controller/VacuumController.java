package rs.raf.springusers.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import rs.raf.springusers.dto.DateRequest;
import rs.raf.springusers.dto.StatusRequest;
import rs.raf.springusers.dto.VacuumIdRequest;
import rs.raf.springusers.dto.VacuumRequest;
import rs.raf.springusers.entities.Status;
import rs.raf.springusers.entities.Vacuum;
import rs.raf.springusers.exceptions.VacuumOperationException;
import rs.raf.springusers.services.VacuumService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1/vacuum")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class VacuumController {
    private final VacuumService vacuumService;
    @PostMapping("/{user_id}/add")
    public ResponseEntity<Vacuum> addVacuum(@PathVariable Long user_id, @RequestBody VacuumRequest vacuumRequest)
    {
        Vacuum vacuum= vacuumService.addVacuum(user_id,vacuumRequest);
        if(vacuum == null)
        {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(vacuum);
    }
    @PostMapping("/search-status")
    public ResponseEntity<List<Vacuum>> searchByStatus(@RequestBody StatusRequest statusRequest)
    {
        List<Status> status = new ArrayList<>();
        if(statusRequest.isOn()) status.add(Status.ON);
        else if(statusRequest.isOff()) status.add(Status.OFF);
        else if (statusRequest.isDischarging()) status.add(Status.DISCHARGE);

        List<Vacuum> vacums = vacuumService.searchVacuumByStatus(status);
        return ResponseEntity.ok(vacums);
    }
    @PostMapping("/{vacuumId}/delete-vacuum")
    public ResponseEntity<String> deleteVacuum(@PathVariable Long vacuumId)
    {
        vacuumService.deactivateVacuum(vacuumId);
        return ResponseEntity.ok("Vacuum deactivated");
    }
    @GetMapping("/search-date")
    public List<Vacuum> searchVacuumsByCreatedAtBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return vacuumService.findVacuumsByCreatedAtBetween(fromDate, toDate);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Vacuum>> searchVacuums(@RequestParam String name, @RequestParam int pageSize) {
        List<Vacuum> result = vacuumService.searchVacuumsByName(name, pageSize);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{vacuum_id}")
    public ResponseEntity<Vacuum> getVacuum(@PathVariable Long vacuum_id)
    {
        return ResponseEntity.ok(vacuumService.findById(vacuum_id));
    }
    //todo
    @PostMapping("/turn-on")
    public ResponseEntity<String> turnOnVacuum(@RequestBody VacuumIdRequest vacuumId)  {
        try {
            vacuumService.turnOnVacuumAsync(vacuumId.getVacuumId());
        } catch (VacuumOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Turning on the vacuum. It will turn on after a delay.");
    }

    @PostMapping("/{vacuumId}/turn-off")
    public ResponseEntity<String> turnOffVacuum(@PathVariable Long vacuumId) {
        try {
            vacuumService.turnOffVacuumAsync(vacuumId);
        } catch (VacuumOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        } catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Turning of the vacuum. It will turn off after a delay.");
    }
    @PostMapping("/{vacuumId}/discharge")
    public ResponseEntity<String> dischargeVacuum(@PathVariable Long vacuumId) {
        try {
            vacuumService.dischargeVacuumAsync(vacuumId);
        } catch (VacuumOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        } catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Discharging the vacuum. It will turn on after a delay.");
    }

    @PostMapping("/{vacuumId}/turn-on-scheduled")
    public ResponseEntity<String> turnOnScheduled(@PathVariable Long vacuumId, @RequestBody DateRequest dateTime)
    {
        try {
            vacuumService.turnOnScheduled(vacuumId, dateTime.getDateTime());
        } catch (VacuumOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        } catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Turn on scheduled");
    }
    @PostMapping("/{vacuumId}/turn-off-scheduled")
    public ResponseEntity<String> turnOffScheduled(@PathVariable Long vacuumId, @RequestBody DateRequest dateTime)
    {
        try {
            vacuumService.turnOffScheduled(vacuumId, dateTime.getDateTime());
        }  catch (VacuumOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        } catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Turn off scheduled");

    }
    @PostMapping("/{vacuumId}/discharge-scheduled")
    public ResponseEntity<String> dischargeScheduled(@PathVariable Long vacuumId, @RequestBody DateRequest dateTime)
    {
        try {
            vacuumService.dischargeScheduled(vacuumId, dateTime.getDateTime());
        } catch (VacuumOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        } catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Discharge scheduled");

    }
    @GetMapping("/{userId}/get-all-vacuums")
    public ResponseEntity<List<Vacuum>> getAllVacuums(@PathVariable Long userId)
    {
        return ResponseEntity.ok(vacuumService.getAllVacuums(userId));
    }
}
