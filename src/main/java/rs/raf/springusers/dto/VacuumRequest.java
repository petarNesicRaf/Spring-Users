package rs.raf.springusers.dto;

import lombok.Data;
import rs.raf.springusers.entities.Status;
@Data
public class VacuumRequest {
    private String name;
    private int active;
}
