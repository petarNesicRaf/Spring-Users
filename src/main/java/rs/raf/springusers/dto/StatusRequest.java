package rs.raf.springusers.dto;

import lombok.Data;

@Data
public class StatusRequest {
    private boolean on;
    private boolean off;
    private boolean discharging;
}
