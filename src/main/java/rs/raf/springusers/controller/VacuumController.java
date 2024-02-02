package rs.raf.springusers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import rs.raf.springusers.dto.DateRequest;
import rs.raf.springusers.dto.VacuumRequest;
import rs.raf.springusers.entities.Vacuum;
import rs.raf.springusers.services.VacuumService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1/vacuum")
@RequiredArgsConstructor
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
    //todo delete sta znaci da je vacuum van sistema??

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

    @PostMapping("/{vacuumId}/turn-on")
    public ResponseEntity<String> turnOnVacuum(@PathVariable Long vacuumId) {
        vacuumService.turnOnVacuumAsync(vacuumId);
        return ResponseEntity.ok("Turning on the vacuum. It will turn on after a delay.");
    }

    @PostMapping("/{vacuumId}/turn-off")
    public ResponseEntity<String> turnOffVacuum(@PathVariable Long vacuumId) {
        vacuumService.turnOffVacuumAsync(vacuumId);
        return ResponseEntity.ok("Turning of the vacuum. It will turn off after a delay.");
    }
    @PostMapping("/{vacuumId}/discharge")
    public ResponseEntity<String> dischargeVacuum(@PathVariable Long vacuumId) {
        vacuumService.dischargeVacuumAsync(vacuumId);
        return ResponseEntity.ok("Discharging the vacuum. It will turn on after a delay.");
    }

    @PostMapping("/{vacuumId}/turn-on-scheduled")
    public ResponseEntity<String> turnOnScheduled(@PathVariable Long vacuumId, @RequestBody DateRequest dateTime)
    {
        vacuumService.turnOnScheduled(vacuumId, dateTime.getDateTime());
        return ResponseEntity.ok("Turn on scheduled");
    }
    @PostMapping("/{vacuumId}/turn-off-scheduled")
    public ResponseEntity<String> turnOffScheduled(@PathVariable Long vacuumId, @RequestBody DateRequest dateTime)
    {
        vacuumService.turnOffScheduled(vacuumId, dateTime.getDateTime());
        return ResponseEntity.ok("Turn off scheduled");

    }
    @PostMapping("/{vacuumId}/discharge-scheduled")
    public ResponseEntity<String> dischargeScheduled(@PathVariable Long vacuumId, @RequestBody DateRequest dateTime)
    {
        vacuumService.dischargeScheduled(vacuumId, dateTime.getDateTime());
        return ResponseEntity.ok("Discharge scheduled");

    }
}
